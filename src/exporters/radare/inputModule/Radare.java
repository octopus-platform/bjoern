package exporters.radare.inputModule;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exporters.radare.inputModule.exceptions.InvalidRadareFunction;
import exporters.structures.annotations.Flag;

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
		String cmd = String.format("pdf @" + Long.toUnsignedString(addr));
		return r2Pipe.cmd(cmd);
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

	private static Flag createFlagFromLine(String nextLine)
	{
		Flag flag = new Flag();

		String[] parts = nextLine.split(" ");
		if (parts.length != 3)
		{
			logger.info("Returning empty flag for line: {}", nextLine);
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
}
