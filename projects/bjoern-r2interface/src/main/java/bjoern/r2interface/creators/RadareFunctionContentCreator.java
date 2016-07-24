package bjoern.r2interface.creators;

import bjoern.r2interface.exceptions.BasicBlockWithoutAddress;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.NodeKey;
import bjoern.structures.edges.ControlFlowEdge;
import bjoern.structures.edges.EdgeTypes;
import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.FunctionContent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RadareFunctionContentCreator
{

	private static final Logger logger = LoggerFactory.getLogger(RadareFunctionContentCreator.class);

	public static FunctionContent createFromJSON(JSONObject jsonFunctionContent)
	{
		FunctionContent content = new FunctionContent();

		initFunctionProperties(content, jsonFunctionContent);
		createBasicBlocks(content, jsonFunctionContent);
		createEdges(content, jsonFunctionContent);

		return content;
	}

	private static void initFunctionProperties(FunctionContent content,
			JSONObject jsonFunctionContent)
	{
		// TODO: Any properties we cannot obtain from the function overview can
		// be added here.
	}

	private static void createBasicBlocks(FunctionContent content,
			JSONObject jsonFunctionContent)
	{
		JSONArray blocks = jsonFunctionContent.getJSONArray("blocks");
		int numberOfBlocks = blocks.length();

		for (int i = 0; i < numberOfBlocks; i++)
		{
			JSONObject block = blocks.getJSONObject(i);
			try
			{
				BasicBlock node = createBasicBlock(block);
				content.registerBasicBlock(node);
			} catch (BasicBlockWithoutAddress e)
			{
				logger.error("Skipping basic block without address:");
			} catch (InvalidRadareFunctionException e)
			{
				logger.error(e.getMessage());
			}
		}
	}

	private static BasicBlock createBasicBlock(JSONObject jsonBlock) throws BasicBlockWithoutAddress,
			InvalidRadareFunctionException
	{

		Long address = JSONUtils.getLongFromObject(jsonBlock, "offset");
		if (address == null)
			throw new BasicBlockWithoutAddress();

		return RadareBasicBlockCreator.createFromJSON(jsonBlock);
	}

	private static void createEdges(FunctionContent content, JSONObject jsonFunctionContent)
	{
		JSONArray blocks = jsonFunctionContent.getJSONArray("blocks");
		int numberOfBlocks = blocks.length();
		for (int i = 0; i < numberOfBlocks; i++)
		{
			JSONObject jsonBlock = blocks.getJSONObject(i);
			createEdgesForBlock(content, jsonBlock);
		}
	}

	private static void createEdgesForBlock(FunctionContent content, JSONObject jsonBlock)
	{
		NodeKey fromBlockKey = getJumpSourceKey(jsonBlock);
		NodeKey jumpBlockKey = getJumpTargetKey(jsonBlock, "jump");
		NodeKey failBlockKey = getJumpTargetKey(jsonBlock, "fail");

		if (fromBlockKey == null || jumpBlockKey == null)
			return;

		if (failBlockKey == null)
		{
			content.addControlFlowEdge(new ControlFlowEdge(fromBlockKey, jumpBlockKey, EdgeTypes.CFLOW));
		} else
		{
			content.addControlFlowEdge(new ControlFlowEdge(fromBlockKey, jumpBlockKey, EdgeTypes.CFLOW_TRUE));
			content.addControlFlowEdge(new ControlFlowEdge(fromBlockKey, failBlockKey, EdgeTypes.CFLOW_FALSE));
		}
	}

	private static NodeKey getJumpSourceKey(JSONObject block)
	{
		Long offset = JSONUtils.getLongFromObject(block, "offset");
		if (offset == null)
		{
			return null;
		}
		return new NodeKey(offset, BjoernNodeTypes.BASIC_BLOCK);
	}

	private static NodeKey getJumpTargetKey(JSONObject block, String type)
	{
		Long toAddr = JSONUtils.getLongFromObject(block, type);
		if (toAddr == null)
		{
			return null;
		}
		return new NodeKey(toAddr, BjoernNodeTypes.BASIC_BLOCK);
	}

}
