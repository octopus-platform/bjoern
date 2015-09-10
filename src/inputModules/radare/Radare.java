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

public class Radare
{
	RCore rCore;
	RAnal analysisResults;

	static
	{
		loadNativeLibraries();
	}

	private static void loadNativeLibraries()
	{
		System.loadLibrary("jr_core");
	}

	public void loadBinary(String filename)
	{
		rCore = new RCore();
		rCore.file_open(filename, 0, BigInteger.ZERO);
		rCore.bin_load(null, BigInteger.ZERO);
	}

	public void analyzeBinary()
	{
		rCore.cmd0("aaa");
		analysisResults = rCore.getAnal();
	}

	public List<BigInteger> getFunctionAddresses()
	{
		if (analysisResults == null)
			throw new RuntimeException("analyzeBinary must be called first");

		RAnalFunctionVector functions = analysisResults.get_fcns();

		return radareFunctionVectorToAddrList(functions);
	}

	private List<BigInteger> radareFunctionVectorToAddrList(
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

	private int getNumberOfFunctions(RAnalFunctionVector functions)
	{
		long numberOfFunctions = functions.size();
		if (numberOfFunctions > Integer.MAX_VALUE)
			throw new RuntimeException("Too many functions");

		return (int) numberOfFunctions;
	}

	public JSONObject getJSONFunctionAt(Long addr)
	{
		// TODO/Bug: we need to handle addresses above 2**63

		String jsonStr = rCore.cmd_str("agj " + addr.toString());

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
		{
			System.err.println("Warning: invalid jsonArray for function: "
					+ addr);
			return null;
		}

		return jsonArray.getJSONObject(0);
	}
}
