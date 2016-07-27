package bjoern.r2interface.creators;


import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.FunctionContent;
import org.json.JSONObject;


public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Long address = jsonFunction.getLong("offset");
		String name = jsonFunction.getString("name");
		FunctionContent content = getFunctionContentFromJSON(jsonFunction);
		Function function = new Function.Builder(address).withName(name).withContent(content).build();
		return function;
	}

	private static FunctionContent getFunctionContentFromJSON(JSONObject jsonFunction)
	{
		try
		{
			return RadareFunctionContentCreator.createFromJSON(jsonFunction);
		} catch (Exception e)
		{
			return new FunctionContent();
		}
	}
}
