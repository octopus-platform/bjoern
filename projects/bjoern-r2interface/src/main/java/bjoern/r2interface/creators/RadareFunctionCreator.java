package bjoern.r2interface.creators;


import bjoern.structures.interpretations.Function;
import org.json.JSONObject;


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
		String name = jsonFunction.getString("name");
		retval.setName(name);
	}

}
