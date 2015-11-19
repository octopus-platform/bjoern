package exporters.radare.inputModule;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import exporters.inputModules.InputModule;
import exporters.radare.inputModule.creators.RadareFunctionContentCreator;
import exporters.radare.inputModule.creators.RadareFunctionCreator;
import exporters.radare.inputModule.exceptions.InvalidRadareFunction;
import exporters.structures.Function;
import exporters.structures.FunctionContent;

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
		String disassembly;

		try
		{
			jsonFunctionContent = Radare.getJSONFunctionContentAt(address);
			disassembly = Radare.getDisassemblyForFunctionAt(address);
		} catch (InvalidRadareFunction e)
		{
			return;
		}

		FunctionContent content = RadareFunctionContentCreator
				.createContentFromJSON(jsonFunctionContent);

		jsonFunctionContent = null;

		content.consumeDisassembly(disassembly);

		function.setContent(content);

	}
}
