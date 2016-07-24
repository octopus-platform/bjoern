package bjoern.input.radare.inputModule;

import bjoern.input.common.InputModule;
import bjoern.structures.NodeKey;
import bjoern.structures.BjoernNodeTypes;
import bjoern.r2interface.Radare;
import bjoern.r2interface.creators.RadareFunctionContentCreator;
import bjoern.r2interface.creators.RadareFunctionCreator;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.FunctionContent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RadareInputModule implements InputModule
{

	private Radare radare = new Radare();
	private static final Logger logger = LoggerFactory.getLogger(RadareInputModule.class);

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
		List<Function> retval = new LinkedList<>();

		JSONArray jsonFunctions = radare.getJSONFunctions();
		int nFunctions = jsonFunctions.length();
		int progressCounter = 0;

		radare.enableEsil();
		for (int i = 0; i < nFunctions; i++)
		{
			JSONObject jsonFunction = jsonFunctions.getJSONObject(i);
			Function function = RadareFunctionCreator
					.createFromJSON(jsonFunction);
			initializeFunctionContents(function);
			retval.add(function);
			logger.info("Processing function (" + ++progressCounter + "/" + nFunctions + ")");
		}
		radare.disableEsil();

		return retval;
	}

	@Override
	public List<Flag> getFlags() throws IOException
	{
		List<Flag> retval = new LinkedList<>();
		radare.askForFlags();
		Flag flag;
		while ((flag = radare.getNextFlag()) != null)
		{
			retval.add(flag);
		}
		return retval;
	}

	private void initializeFunctionContents(Function function)
			throws IOException
	{

		try
		{
			Long address = function.getAddress();
			JSONObject jsonFunctionContent = radare.getJSONFunctionContentAt(address);
			FunctionContent content = RadareFunctionContentCreator.createFromJSON(jsonFunctionContent);
			function.setContent(content);
		} catch (InvalidRadareFunctionException e)
		{
			logger.error(e.getMessage());
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
				NodeKey sourceKey = new NodeKey(referenceJSONObject.getLong("address"), BjoernNodeTypes.INSTRUCTION);
				NodeKey destinationKey = new NodeKey(referenceJSONObject.getJSONArray("locations").getLong(0),
						BjoernNodeTypes.INSTRUCTION);
				callReferences.add(new CallRef(sourceKey, destinationKey));
			}
		}
		return callReferences;
	}
}
