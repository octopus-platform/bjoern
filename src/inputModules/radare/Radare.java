package inputModules.radare;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.radare.radare2.RAnal;
import org.radare.radare2.RAnalFunction;
import org.radare.radare2.RAnalFunctionVector;
import org.radare.radare2.RCore;

import exceptions.radareInput.InvalidRadareFunction;

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
		rCore.cmd0("aaa");
		analysisResults = rCore.getAnal();
	}

	public static List<BigInteger> getFunctionAddresses()
	{
		if (analysisResults == null)
			throw new RuntimeException("analyzeBinary must be called first");

		RAnalFunctionVector functions = analysisResults.get_fcns();

		return radareFunctionVectorToAddrList(functions);
	}

	private static List<BigInteger> radareFunctionVectorToAddrList(
			RAnalFunctionVector functions)
	{
		int numberOfFunctions = getNumberOfFunctions(functions);

		List<BigInteger> list = new LinkedList<BigInteger>();
		for (int i = 0; i < numberOfFunctions; i++)
		{
			RAnalFunction function = functions.get(i);
			BigInteger addr = function.getAddr();
			list.add(addr);
		}
		return list;
	}

	private static int getNumberOfFunctions(RAnalFunctionVector functions)
	{
		long numberOfFunctions = functions.size();
		if (numberOfFunctions > Integer.MAX_VALUE)
			throw new RuntimeException("Too many functions");

		return (int) numberOfFunctions;
	}

	public static JSONObject getJSONFunctionAt(Long addr)
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
}
