package bjoern.input.radare.inputModule;

import bjoern.input.common.InputModule;
import bjoern.structures.NodeKey;
import bjoern.structures.BjoernNodeTypes;
import bjoern.r2interface.Radare;
import bjoern.r2interface.creators.RadareFlagCreator;
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
import java.util.Iterator;
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

	public Iterator<Function> getFunctions() throws IOException
	{
		return new Iterator<Function>()
		{
			private JSONArray jsonFunctions = radare.getJSONFunctions();
			private int nextFunction = 0;

			@Override
			public boolean hasNext()
			{
				return nextFunction < jsonFunctions.length();
			}

			@Override
			public Function next()
			{
				JSONObject jsonFunctionObject = jsonFunctions.getJSONObject(nextFunction++);
				Function function = RadareFunctionCreator.createFromJSON(jsonFunctionObject);
				try
				{
					initializeFunctionContents(function);
				} catch (IOException e)
				{
				}
				return function;
			}
		};
	}

	@Override
	public Iterator<Flag> getFlags() throws IOException
	{
		return new Iterator<Flag>() {

			private JSONArray jsonFlags = radare.getFlags();
			private int nextFlag = 0;

			@Override
			public boolean hasNext()
			{
				return nextFlag < jsonFlags.length();
			}

			@Override
			public Flag next()
			{
				JSONObject jsonFlagObject = jsonFlags.getJSONObject(nextFlag++);
				Flag flag = RadareFlagCreator.createFromJSON(jsonFlagObject);
				return flag;
			}
		};
	}

	@Override
	public Iterator<CallRef> getCallReferences() throws IOException
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
		return callReferences.iterator();
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
}
