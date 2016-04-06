package bjoern.input.radare.inputModule.creators;


import org.json.JSONObject;

import bjoern.structures.interpretations.Function;


public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Function retval = new Function(jsonFunction.getLong("offset"));

		initFunctionInfo(jsonFunction, retval);

		return retval;
	}

	private static void initFunctionInfo(JSONObject jsonFunction,
			Function retval)
	{
		long addr = jsonFunction.getLong("offset");
		String name = jsonFunction.getString("name");
		retval.setAddr(addr);
		retval.setName(name);
	}

}
