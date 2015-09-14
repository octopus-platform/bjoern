package inputModules.radare;

import org.json.JSONArray;
import org.json.JSONObject;

import structures.Function;

public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Function retval = new Function();

		initFunctionInfo(jsonFunction, retval);
		initReferences(jsonFunction, retval);

		return retval;
	}

	private static void initReferences(JSONObject jsonFunction, Function retval)
	{
		JSONArray callRefArray = jsonFunction.getJSONArray("callrefs");

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
