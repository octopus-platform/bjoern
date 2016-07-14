package bjoern.input.radare.inputModule;

import bjoern.input.common.InputModule;
import bjoern.nodeStore.NodeKey;
import bjoern.nodeStore.NodeTypes;
import bjoern.r2interface.Radare;
import bjoern.r2interface.RadareDisassemblyParser;
import bjoern.r2interface.creators.RadareFunctionContentCreator;
import bjoern.r2interface.creators.RadareFunctionCreator;
import bjoern.r2interface.exceptions.EmptyDisassembly;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.interpretations.DisassembledFunction;
import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.FunctionContent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RadareInputModule implements InputModule
{

	private Radare radare = new Radare();

	@Override
	public void initialize(String filename, String projectFilename) throws IOException
	{
		radare.loadBinary(filename);

		if (projectFilename != null)
		{
			radare.loadProject(projectFilename);
		} else
		{
			radare.analyzeBinary();
		}
	}

	@Override
	public List<Function> getFunctions() throws IOException
	{
		List<Function> retval = new LinkedList<Function>();
		JSONArray jsonFunctions = radare.getJSONFunctions();
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
		radare.askForFlags();
		Flag flag;
		while ((flag = radare.getNextFlag()) != null)
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
			jsonFunctionContent = radare.getJSONFunctionContentAt(address);
			disassemblyStr = radare.getDisassemblyForFunctionAt(address);
			radare.enableEsil();
			esilDisassemblyStr = radare.getDisassemblyForFunctionAt(address);
			radare.disableEsil();
		} catch (InvalidRadareFunctionException e)
		{
			return;
		}

		FunctionContent content = RadareFunctionContentCreator
				.createContentFromJSON(jsonFunctionContent, address);

		jsonFunctionContent = null;

		generateDisassembly(address, disassemblyStr, content);
		generateESILDisassembly(address, esilDisassemblyStr, content);

		content.updateInstructionsFromDisassembly();

		function.setContent(content);

	}

	private void generateESILDisassembly(Long address, String esilDisassemblyStr, FunctionContent content)
	{
		try
		{
			RadareDisassemblyParser parser = new RadareDisassemblyParser();
			DisassembledFunction func = parser.parseFunction(esilDisassemblyStr, address);
			content.setDisassembledEsilFunction(func);
		} catch (EmptyDisassembly e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateDisassembly(Long address, String disassemblyStr, FunctionContent content)
	{
		try
		{
			RadareDisassemblyParser parser = new RadareDisassemblyParser();
			DisassembledFunction func = parser.parseFunction(disassemblyStr, address);
			content.setDisassembledFunction(func);
		} catch (EmptyDisassembly e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finish(String outputDir)
	{
		try
		{
			saveRadareProject(outputDir);
			radare.shutdown();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void saveRadareProject(String outputDir) throws IOException
	{
		Path cwd = Paths.get(outputDir).toAbsolutePath().normalize();
		String projectFilename = cwd.toString() + File.separator + "radareProject";
		radare.saveProject(projectFilename);
	}

	@Override
	public List<CallRef> getCallReferences() throws IOException
	{
		List<CallRef> callReferences = new LinkedList<>();
		JSONArray references = radare.getReferences();
		for (int i = 0; i < references.length(); i++)
		{
			JSONObject referenceJSONObject = references.getJSONObject(i);
			if (referenceJSONObject.getString("type").equals("ref.code.call"))
			{
				NodeKey sourceKey = new NodeKey(referenceJSONObject.getLong("address"), NodeTypes.INSTRUCTION);
				NodeKey destinationKey = new NodeKey(referenceJSONObject.getJSONArray("locations").getLong(0),
						NodeTypes.INSTRUCTION);
				callReferences.add(new CallRef(sourceKey, destinationKey));
			}
		}
		return callReferences;
	}
}
