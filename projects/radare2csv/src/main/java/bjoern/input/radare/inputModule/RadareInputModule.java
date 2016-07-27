package bjoern.input.radare.inputModule;

import bjoern.input.common.InputModule;
import bjoern.r2interface.Radare;
import bjoern.r2interface.creators.RadareFlagCreator;
import bjoern.r2interface.creators.RadareFunctionCreator;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.NodeKey;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.interpretations.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

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
				Long functionOffset = jsonFunctionObject.getLong("offset");
				Function function;
				try
				{
					JSONObject functionJSON = radare.getJSONFunctionContentAt(functionOffset);
					function = RadareFunctionCreator.createFromJSON(functionJSON);
				} catch (IOException | InvalidRadareFunctionException e)
				{
					function = RadareFunctionCreator.createFromJSON(jsonFunctionObject);
				}
				return function;
			}
		};
	}

	@Override
	public Iterator<Flag> getFlags() throws IOException
	{
		return new Iterator<Flag>()
		{
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
		}

				;
	}

	@Override
	public Iterator<CallRef> getCallReferences() throws IOException
	{
		return new Iterator<CallRef>()
		{

			private JSONArray references = radare.getReferences();
			private JSONObject jsonReference = null;
			private int nextReference = 0;

			@Override
			public boolean hasNext()
			{
				while (nextReference < references.length())
				{
					jsonReference = references.getJSONObject(nextReference++);
					if (isCallReference(jsonReference))
					{
						return true;
					}
				}
				return false;
			}

			private boolean isCallReference(JSONObject jsonReference)
			{
				return jsonReference.getString("type").equals("ref.code.call");
			}

			@Override
			public CallRef next()
			{
				NodeKey sourceKey = new NodeKey(jsonReference.getLong("address"), BjoernNodeTypes.INSTRUCTION);
				NodeKey destinationKey = new NodeKey(jsonReference.getJSONArray("locations").getLong(0),
						BjoernNodeTypes.INSTRUCTION);
				return new CallRef(sourceKey, destinationKey);
			}
		};
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
