package exporters.radare.inputModule;

import java.math.BigInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.radare.radare2.RAnal;
import org.radare.radare2.RCore;

import exporters.radare.inputModule.exceptions.InvalidRadareFunction;

public class Radare
{
	static RCore rCore;
	static RAnal analysisResults;

	static
	{
		loadNativeLibraries();
	}

	private static void loadNativeLibraries()
	{
		System.loadLibrary("jr_core");
	}

	public static void loadBinary(String filename)
	{
		rCore = new RCore();
		rCore.file_open(filename, 0, BigInteger.ZERO);
		rCore.bin_load(null, BigInteger.ZERO);
	}

	public static void analyzeBinary()
	{
		setRadareOptions();

		rCore.cmd0("aaa");
		analysisResults = rCore.getAnal();
	}

	private static void setRadareOptions()
	{
		rCore.cmd0("e scr.color = false");
		rCore.cmd0("e asm.bytes = false");
		rCore.cmd0("e asm.lines = false");
		rCore.cmd0("e asm.fcnlines = false");
		rCore.cmd0("e asm.xrefs = false");
		rCore.cmd0("e asm.lbytes = false");
		rCore.cmd0("e asm.indentspace = 0");
	}

	public static JSONArray getJSONFunctions()
	{
		String str = rCore.cmd_str("aflj");
		return new JSONArray(str);
	}

	public static JSONObject getJSONFunctionContentAt(Long addr)
			throws InvalidRadareFunction
	{

		String jsonStr = rCore.cmd_str("agj " + Long.toUnsignedString(addr));

		JSONArray jsonArray;
		try
		{
			jsonArray = new JSONArray(jsonStr);
		} catch (JSONException ex)
		{
			return null;
		}

		if (jsonArray.length() != 1)
			throw new InvalidRadareFunction();

		return jsonArray.getJSONObject(0);
	}

	public static String getDisassemblyForFunctionAt(Long addr)
	{
		// It would be much nicer if we could obtain an array representing the
		// disassembly as opposed to a single string.
		String cmd = String.format("pdf @" + Long.toUnsignedString(addr));
		return rCore.cmd_str(cmd);
	}
}
