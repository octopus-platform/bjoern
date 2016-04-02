package bjoern.input.radare.inputModule;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import bjoern.input.common.InputModule;
import bjoern.input.common.structures.annotations.Flag;
import bjoern.input.common.structures.edges.CallRef;
import bjoern.input.common.structures.edges.Xref;
import bjoern.input.common.structures.interpretations.Function;
import bjoern.input.common.structures.interpretations.FunctionContent;
import bjoern.input.radare.inputModule.creators.RadareFunctionContentCreator;
import bjoern.input.radare.inputModule.creators.RadareFunctionCreator;
import bjoern.input.radare.inputModule.exceptions.InvalidRadareFunction;

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
		String disassemblyStr;
		String esilDisassemblyStr;

		try
		{
			jsonFunctionContent = Radare.getJSONFunctionContentAt(address);
			disassemblyStr = Radare.getDisassemblyForFunctionAt(address);
			Radare.enableEsil();
			esilDisassemblyStr = Radare.getDisassemblyForFunctionAt(address);
			Radare.disableEsil();
		}
		catch (InvalidRadareFunction e)
		{
			return;
		}

		FunctionContent content = RadareFunctionContentCreator
				.createContentFromJSON(jsonFunctionContent, address);

		jsonFunctionContent = null;

		content.consumeDisassembly(disassemblyStr);
		content.consumeEsilDisassembly(esilDisassemblyStr);

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
	public List<Xref> getCrossReferences() throws IOException
	{
		List<Xref> retval = new LinkedList<Xref>();
		Radare.askForCrossReferences();
		List<Xref> xefs;

		while ((xefs = Radare.getNextCrossReferences()) != null)
		{
			retval.addAll(xefs);
		}

		for(Xref r : retval)
		{
			if(r instanceof CallRef){
				CallRef callRef = (CallRef) r;
				callRef.initializeSourceInstruction();
			}
		}

		return retval;
	}

}
