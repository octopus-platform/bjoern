package exporters.radare.inputModule;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import exporters.InputModule;
import exporters.radare.inputModule.creators.RadareFunctionContentCreator;
import exporters.radare.inputModule.creators.RadareFunctionCreator;
import exporters.radare.inputModule.exceptions.InvalidRadareFunction;
import exporters.structures.annotations.Flag;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.interpretations.Function;
import exporters.structures.interpretations.FunctionContent;

public class RadareInputModule implements InputModule
{

	@Override
	public void initialize(String filename) throws IOException
	{
		Radare.loadBinary(filename);
		Radare.analyzeBinary();
	}

	@Override
	public List<Function> getFunctions() throws IOException
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
	public List<Flag> getFlags() throws IOException
	{
		List<Flag> retval = new LinkedList<Flag>();
		Radare.askForFlags();
		Flag flag;
		while ((flag = Radare.getNextFlag()) != null)
		{
			retval.add(flag);
		}
		return retval;
	}

	@Override
	public void initializeFunctionContents(Function function)
			throws IOException
	{
		Long address = function.getAddress();
		JSONObject jsonFunctionContent;
		String disassembly;

		try
		{
			jsonFunctionContent = Radare.getJSONFunctionContentAt(address);
			disassembly = Radare.getDisassemblyForFunctionAt(address);
		}
		catch (InvalidRadareFunction e)
		{
			return;
		}

		FunctionContent content = RadareFunctionContentCreator
				.createContentFromJSON(jsonFunctionContent, address);

		jsonFunctionContent = null;

		content.consumeDisassembly(disassembly);

		function.setContent(content);

	}

	@Override
	public void finish()
	{
		try
		{
			Radare.shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public List<DirectedEdge> getCrossReferences() throws IOException
	{
		List<DirectedEdge> retval = new LinkedList<DirectedEdge>();
		Radare.askForCrossReferences();
		List<DirectedEdge> edges;

		while ((edges = Radare.getNextCrossReferences()) != null)
		{
			retval.addAll(edges);
		}
		return retval;
	}

}
