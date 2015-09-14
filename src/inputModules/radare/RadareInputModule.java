package inputModules.radare;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptions.radareInput.InvalidRadareFunction;
import inputModules.InputModule;
import structures.Function;
import structures.FunctionContent;

public class RadareInputModule implements InputModule
{

	@Override
	public void initialize(String filename)
	{
		Radare.loadBinary(filename);
		Radare.analyzeBinary();
	}

	@Override
	public List<Function> getFunctions()
	{
		List<Function> retval = new LinkedList<Function>();
		JSONArray jsonFunctions = Radare.getJSONFunctions();
		int nFunctions = jsonFunctions.length();
		for (int i = 0; i < nFunctions; i++)
		{
			JSONObject jsonFunction = jsonFunctions.getJSONObject(i);
			Function function = RadareFunctionCreator
					.createFromJSON(jsonFunction);
			retval.add(function);
		}

		return retval;
	}

	@Override
	public void initializeFunctionContents(Function function)
	{
		Long address = function.getAddress();
		JSONObject jsonFunctionContent;

		try
		{
			jsonFunctionContent = Radare.getJSONFunctionContentAt(address);
		} catch (InvalidRadareFunction e)
		{
			return;
		}

		FunctionContent content = RadareFunctionContentCreator
				.createContentFromJSON(jsonFunctionContent);

		function.setContent(content);

	}
}
