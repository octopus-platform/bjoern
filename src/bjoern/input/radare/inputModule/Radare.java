package bjoern.input.radare.inputModule;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bjoern.input.common.nodeStore.NodeKey;
import bjoern.input.common.nodeStore.NodeTypes;
import bjoern.input.common.structures.annotations.Flag;
import bjoern.input.common.structures.edges.CallRef;
import bjoern.input.common.structures.edges.EdgeTypes;
import bjoern.input.common.structures.edges.Xref;
import bjoern.input.radare.inputModule.exceptions.InvalidRadareFunction;

public class Radare
{
	static R2Pipe r2Pipe;

	private static final Logger logger = LoggerFactory.getLogger(Radare.class);

	public static void loadBinary(String filename) throws IOException
	{
		r2Pipe = new R2Pipe(filename);
	}

	public static void analyzeBinary() throws IOException
	{
		setRadareOptions();
		r2Pipe.cmd("aaa");
	}

	private static void setRadareOptions() throws IOException
	{
		r2Pipe.cmd("e scr.color = false");
		r2Pipe.cmd("e asm.bytes = false");
		r2Pipe.cmd("e asm.lines = false");
		r2Pipe.cmd("e asm.fcnlines = false");
		r2Pipe.cmd("e asm.xrefs = false");
		r2Pipe.cmd("e asm.lbytes = false");
		r2Pipe.cmd("e asm.indentspace = 0");
	}

	public static JSONArray getJSONFunctions() throws IOException
	{
		String str = r2Pipe.cmd("aflj");
		return new JSONArray(str);
	}

	public static JSONObject getJSONFunctionContentAt(Long addr)
			throws InvalidRadareFunction, IOException
	{

		String jsonStr = r2Pipe.cmd("agj " + Long.toUnsignedString(addr));

		JSONArray jsonArray;
		try
		{
			jsonArray = new JSONArray(jsonStr);
		}
		catch (JSONException ex)
		{
			return null;
		}

		if (jsonArray.length() != 1)
			throw new InvalidRadareFunction();

		return jsonArray.getJSONObject(0);
	}

	public static String getDisassemblyForFunctionAt(Long addr)
			throws IOException
	{
		// It would be much nicer if we could obtain an array representing the
		// disassembly as opposed to a single string.
		String cmd = "pdf @" + Long.toUnsignedString(addr);
		return r2Pipe.cmd(cmd);
	}

	public static String getDisassemblyForInstructionAt(Long addr) throws IOException
	{
		String cmd = "pd 1 @" + Long.toUnsignedString(addr);
		return r2Pipe.cmd(cmd).trim();
	}

	public static void shutdown() throws Exception
	{
		r2Pipe.quit();
	}

	public static void askForFlags() throws IOException
	{
		r2Pipe.cmdNoResponse("f");
	}

	public static Flag getNextFlag() throws IOException
	{
		String nextLine = r2Pipe.readNextLine();
		if (nextLine.length() == 0 || nextLine.endsWith("\0"))
			return null;
		return createFlagFromLine(nextLine);
	}

	private static Flag createFlagFromLine(String line)
	{
		Flag flag = new Flag();

		String[] parts = line.split(" ");
		if (parts.length != 3)
		{
			logger.info("Returning empty flag for line: {}", line);
			return flag;
		}

		long addr = Long.decode(parts[0]);
		int length = Integer.parseInt(parts[1]);
		String value = parts[2];

		flag.setAddr(addr);
		flag.setLength(length);
		flag.setValue(value);

		return flag;
	}

	public static void askForCrossReferences() throws IOException
	{
		r2Pipe.cmdNoResponse("ax");
		// skip first line
		r2Pipe.readNextLine();
	}

	public static List<Xref> getNextCrossReferences() throws IOException
	{
		while(true){
			String nextLine = r2Pipe.readNextLine();
			if (nextLine.length() == 0 || nextLine.endsWith("\0"))
				return null;
			List<Xref> newXrefs = createXrefsFromLine(nextLine);

			if(newXrefs == null)
				continue;

			return newXrefs;
		}
	}

	public static void enableEsil() throws IOException
	{
		r2Pipe.cmd("e asm.esil=true");
	}

	public static void disableEsil() throws IOException
	{
		r2Pipe.cmd("e asm.esil=false");
	}

	private static List<Xref> createXrefsFromLine(String line)
	{

		String[] parts = line.split("=");
		int lastDotPosition = parts[0].lastIndexOf(".");
		String type = parts[0].substring(0, lastDotPosition);

		// TODO: handle other types of edges here
		// ref.data.mem
		// xref.data.mem
		// ref.code.call
		// xref.code.call
		// ref.code.jmp
		// xref.code.jmp

		if(!type.equals("ref.code.call"))
			return null;

		String destList = parts[1];
		String[] destinations = destList.split(",");
		Long sourceId = Long.decode(parts[0].substring(lastDotPosition + 1));

		LinkedList<Xref> retval = new LinkedList<Xref>();
		for(String dest : destinations)
		{
			Long destId = Long.decode(dest);
			Xref xref = createCallRef(destId, sourceId);
			retval.add(xref);
		}

		return retval;
	}

	private static CallRef createCallRef(Long dest, Long source)
	{
		CallRef xref = new CallRef();
		xref.setType(EdgeTypes.CALL);
		xref.setSourceKey(new NodeKey(source, NodeTypes.INSTRUCTION));
		xref.setDestKey(new NodeKey(dest, NodeTypes.ROOT));
		return xref;
	}

}
