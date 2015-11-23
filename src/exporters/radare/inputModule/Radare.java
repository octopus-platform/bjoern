package exporters.radare.inputModule;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exporters.radare.inputModule.exceptions.InvalidRadareFunction;

public class Radare
{
	static R2Pipe r2Pipe;

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

}
