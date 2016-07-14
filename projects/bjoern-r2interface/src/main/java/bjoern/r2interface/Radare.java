package bjoern.r2interface;

import bjoern.nodeStore.NodeKey;
import bjoern.nodeStore.NodeTypes;
import bjoern.r2interface.architectures.Architecture;
import bjoern.r2interface.architectures.X64Architecture;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.edges.EdgeTypes;
import bjoern.structures.edges.Reference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Radare
{
	R2Pipe r2Pipe;

	private static final Logger logger = LoggerFactory.getLogger(Radare.class);

	public void loadBinary(String filename) throws IOException
	{
		r2Pipe = new R2Pipe(filename);
		setRadareOptions();
	}

	public void analyzeBinary() throws IOException
	{
		r2Pipe.cmd("aaa");
	}

	private void setRadareVariable(String group, String variable, String value) throws IOException
	{
		r2Pipe.cmd("e " + group + "." + variable + " = " + value);
	}

	private String getRadareVariable(String group, String variable) throws IOException
	{
		return r2Pipe.cmd("e " + group + " " + variable);
	}

	private void setASMVariable(String variable, String value) throws IOException
	{
		setRadareVariable("asm", variable, value);
	}

	private String getASMVariable(String variable) throws IOException
	{
		return getRadareVariable("asm", variable);
	}

	private void setSCRVariable(String variable, String value) throws IOException
	{
		setRadareVariable("scr", variable, value);
	}

	private void setRadareOptions() throws IOException
	{
		setSCRVariable("color", "false");
		setASMVariable("bytes", "false");
		setASMVariable("lines", "false");
		setASMVariable("fcnlines", "false");
		setASMVariable("xrefs", "false");
		setASMVariable("lbytes", "false");
		setASMVariable("indentspace", "0");
	}

	public void saveProject(String projectFilename) throws IOException
	{
		r2Pipe.cmd("Ps " + projectFilename);
	}

	public void loadProject(String projectFilename) throws IOException
	{
		r2Pipe.cmd("Po " + projectFilename);
		// This is a workaround: for some reason, when loading a
		// project, r2 goes out of quiet mode
		setSCRVariable("interactive", "false");
		setSCRVariable("prompt", "false");
		setSCRVariable("color", "false");
	}

	public Architecture getArchitecture() throws IOException
	{
		String arch = getASMVariable("arch");
		String bits = getASMVariable("bits");

		// TODO: Actually take a look at `arch` and `bits` to decide which architecture to return
		return new X64Architecture();
	}

	public JSONArray getJSONFunctions() throws IOException
	{
		String str = r2Pipe.cmd("aflj");
		return new JSONArray(str);
	}

	public JSONObject getJSONFunctionContentAt(Long addr)
			throws InvalidRadareFunctionException, IOException
	{

		String jsonStr = r2Pipe.cmd("agj " + Long.toUnsignedString(addr));

		JSONArray jsonArray;
		try
		{
			jsonArray = new JSONArray(jsonStr);
		} catch (JSONException ex)
		{
			return null;
		}

		if (jsonArray.length() != 1)
			throw new InvalidRadareFunctionException();

		return jsonArray.getJSONObject(0);
	}

	public String getDisassemblyForFunctionAt(Long addr)
			throws IOException
	{
		// It would be much nicer if we could obtain an array representing the
		// disassembly as opposed to a single string.
		String cmd = "pdf @" + Long.toUnsignedString(addr);
		return r2Pipe.cmd(cmd);
	}

	public String getDisassemblyForInstructionAtAddress(Long addr) throws IOException
	{
		String cmd = "pd 1 @" + Long.toUnsignedString(addr);
		return r2Pipe.cmd(cmd).trim();
	}

	public void shutdown() throws Exception
	{
		r2Pipe.quit();
	}

	public void askForFlags() throws IOException
	{
		r2Pipe.cmdNoResponse("f");
	}

	public Flag getNextFlag() throws IOException
	{
		String nextLine = r2Pipe.readNextLine();
		if (nextLine.length() == 0 || nextLine.endsWith("\0"))
			return null;
		return createFlagFromLine(nextLine);
	}

	private Flag createFlagFromLine(String line)
	{
		String[] parts = line.split(" ");
		long addr = Long.decode(parts[0]);
		Flag flag = new Flag(addr);
		if (parts.length != 3)
		{
			logger.info("Returning empty flag for line: {}", line);
			return flag;
		}

		int length = Integer.parseInt(parts[1]);
		String value = parts[2];
		flag.setLength(length);
		flag.setValue(value);

		return flag;
	}

	public void askForCrossReferences() throws IOException
	{
		r2Pipe.cmdNoResponse("ax");
		// skip first line
		r2Pipe.readNextLine();
	}

	public List<Reference> getNextCrossReferences() throws IOException
	{
		while (true)
		{
			String nextLine = r2Pipe.readNextLine();
			if (nextLine.length() == 0 || nextLine.endsWith("\0"))
				return null;
			List<Reference> newReferences = createXrefsFromLine(nextLine);

			if (newReferences == null)
				continue;

			return newReferences;
		}
	}

	public void enableEsil() throws IOException
	{
		setASMVariable("esil", "true");
	}

	public void disableEsil() throws IOException
	{
		setASMVariable("esil", "false");
	}

	public void resetEsilState() throws IOException
	{
		r2Pipe.cmd("ar0");
		r2Pipe.cmd("aei");
		r2Pipe.cmd("aeim");
	}

	public String runEsilCode(String esilCode) throws IOException
	{
		return r2Pipe.cmd(String.format("\"ae %s\"", esilCode));
	}

	public String getRegisterValue(String registerStr) throws IOException
	{
		return r2Pipe.cmd(String.format("ar %s", registerStr));
	}

	private List<Reference> createXrefsFromLine(String line)
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

		if (!type.equals("ref.code.call"))
			return null;

		String destList = parts[1];
		String[] destinations = destList.split(",");
		Long sourceId = Long.decode(parts[0].substring(lastDotPosition + 1));

		LinkedList<Reference> retval = new LinkedList<Reference>();
		for (String dest : destinations)
		{
			Long destId = Long.decode(dest);
			Reference reference = createCallRef(destId, sourceId);
			retval.add(reference);
		}

		return retval;
	}

	private CallRef createCallRef(Long dest, Long source)
	{
		CallRef xref = new CallRef(new NodeKey(source, NodeTypes.INSTRUCTION),
				new NodeKey(dest, NodeTypes.INSTRUCTION), EdgeTypes.CALL);
		return xref;
	}

	public List<String> getRegistersWritten(String addr) throws IOException
	{
		String cmd = "aeaw @ " + addr;
		return cmdAndSplitResultAtWhitespace(cmd);
	}

	public List<String> getRegistersRead(String addr) throws IOException
	{
		String cmd = "aear @ " + addr;
		return cmdAndSplitResultAtWhitespace(cmd);
	}


	private List<String> cmdAndSplitResultAtWhitespace(String cmd) throws IOException
	{
		String registers = r2Pipe.cmd(cmd).trim();
		if (registers.length() == 0)
			return new LinkedList<String>();

		String[] registersAr = registers.split(" ");
		return Arrays.asList(registersAr);
	}

}
