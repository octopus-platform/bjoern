package bjoern.input.radare.inputModule;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import bjoern.input.common.InputModule;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.edges.Xref;
import bjoern.structures.interpretations.DisassembledFunction;
import bjoern.structures.interpretations.DisassemblyLine;
import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.FunctionContent;
import bjoern.r2interface.Radare;
import bjoern.r2interface.RadareDisassemblyParser;
import bjoern.r2interface.creators.RadareFunctionContentCreator;
import bjoern.r2interface.creators.RadareFunctionCreator;
import bjoern.r2interface.exceptions.EmptyDisassembly;
import bjoern.r2interface.exceptions.InvalidRadareFunction;

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
		
		generateDisassembly(address, disassemblyStr, content);
		generateESILDisassembly(address, esilDisassemblyStr, content);

		function.setContent(content);

	}

	private void generateESILDisassembly(Long address, String esilDisassemblyStr, FunctionContent content) {
		try {
			RadareDisassemblyParser parser = new RadareDisassemblyParser();
			DisassembledFunction func = parser.parseFunction(esilDisassemblyStr, address);
			content.setDisassembledEsilFunction(func);
		} catch (EmptyDisassembly e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateDisassembly(Long address, String disassemblyStr, FunctionContent content) {
		try {
			RadareDisassemblyParser parser = new RadareDisassemblyParser();		
			DisassembledFunction func = parser.parseFunction(disassemblyStr, address);
			content.setDisassembledFunction(func);
		} catch (EmptyDisassembly e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		List<Xref> crossReferences = new LinkedList<Xref>();
		Radare.askForCrossReferences();
		List<Xref> xefs;

		while ((xefs = Radare.getNextCrossReferences()) != null)
		{
			crossReferences.addAll(xefs);
		}

		for(Xref r : crossReferences)
		{
			if(r instanceof CallRef)	
				initializeCallRefInstruction(r);			
		}

		return crossReferences;
	}

	private void initializeCallRefInstruction(Xref xref) throws IOException
	{
		CallRef callRef = (CallRef) xref;
		long addr = callRef.getSourceKey().getAddress();
		String line = Radare.getDisassemblyForInstructionAt(addr);
		RadareDisassemblyParser parser = new RadareDisassemblyParser();
		DisassemblyLine parsedInstruction = parser.parseInstruction(line);
		callRef.setDisassemblyLine(parsedInstruction);
	}

}
