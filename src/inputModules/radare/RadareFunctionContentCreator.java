package inputModules.radare;

import nodeStore.NodeStore;
import nodeStore.NodeTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import structures.BasicBlock;
import structures.FunctionContent;
import structures.edges.EdgeTypes;
import exceptions.radareInput.BasicBlockWithoutAddress;
import exceptions.radareInput.EdgeTargetNotFound;

public class RadareFunctionContentCreator
{

	public static FunctionContent createContentFromJSON(
			JSONObject jsonFunctionContent)
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
				createBasicBlock(content, block);
			}
			catch (BasicBlockWithoutAddress e)
			{
				System.err.println("Skipping basic block without address");
				continue;
			}
		}
	}

	private static void createBasicBlock(FunctionContent content,
			JSONObject jsonBlock) throws BasicBlockWithoutAddress
	{

		Long address = JSONUtils.getLongFromObject(jsonBlock, "offset");
		if (address == null)
			throw new BasicBlockWithoutAddress();

		BasicBlock node = createBlockOrTakeExisting(jsonBlock, address);
		content.registerBasicBlock(address, node);

	}

	private static BasicBlock createBlockOrTakeExisting(JSONObject block,
			Long address)
	{
		BasicBlock node;
		node = (BasicBlock) NodeStore.getNodeForAddressAndType(address,
				NodeTypes.BASIC_BLOCK);

		if (node == null)
		{
			node = RadareBasicBlockCreator.createFromJSON(block);
			NodeStore.addNode(node);
		}
		return node;
	}

	private static void createEdges(FunctionContent content,
			JSONObject jsonFunctionContent)
	{
		JSONArray blocks = jsonFunctionContent.getJSONArray("blocks");
		int numberOfBlocks = blocks.length();
		for (int i = 0; i < numberOfBlocks; i++)
		{
			JSONObject jsonBlock = blocks.getJSONObject(i);
			try
			{
				createEdgesForBlock(content, jsonBlock);
			}
			catch (EdgeTargetNotFound e)
			{
				// If target wasn't even given, we're fine.
				if (!e.isTargetGiven())
					continue;
				// Otherwise, the target was given but we were unable to resolve
				// it. Store this edge as an unresolved edge.

				BasicBlock basicBlock = getBasicBlockForJSONBlock(jsonBlock);
				content.addUnresolvedEdge(basicBlock.getAddress(),
						e.getAddress(), e.getType());
			}
		}
	}

	private static void createEdgesForBlock(FunctionContent content,
			JSONObject jsonBlock) throws EdgeTargetNotFound
	{
		int numberOfEdges = 2;

		BasicBlock fromBlock = getBasicBlockForJSONBlock(jsonBlock);
		BasicBlock jumpBlock = getJumpTarget(jsonBlock, "jump");
		BasicBlock failBlock = null;

		try
		{
			failBlock = getJumpTarget(jsonBlock, "fail");
		}
		catch (EdgeTargetNotFound ex)
		{
			numberOfEdges = 1;
		}

		if (numberOfEdges == 1)
			content.addEdge(fromBlock, jumpBlock, EdgeTypes.CFLOW);
		else
		{
			assert (failBlock != null);
			content.addEdge(fromBlock, jumpBlock, EdgeTypes.CFLOW_TRUE);
			content.addEdge(fromBlock, failBlock, EdgeTypes.CFLOW_FALSE);
		}

	}

	private static BasicBlock getBasicBlockForJSONBlock(JSONObject block)
	{
		Long blockAddr = JSONUtils.getLongFromObject(block, "offset");
		assert (blockAddr != null);

		BasicBlock fromBlock = (BasicBlock) NodeStore.getNodeForAddressAndType(
				blockAddr, NodeTypes.BASIC_BLOCK);

		if (fromBlock == null)
			throw new RuntimeException("From-node not in store.");

		return fromBlock;
	}

	private static BasicBlock getJumpTarget(JSONObject block, String type)
			throws EdgeTargetNotFound
	{
		Long toAddr = JSONUtils.getLongFromObject(block, type);
		if (toAddr == null)
			throw new EdgeTargetNotFound(false, 0, type);

		BasicBlock to = (BasicBlock) NodeStore.getNodeForAddressAndType(toAddr,
				NodeTypes.BASIC_BLOCK);
		if (to == null)
			throw new EdgeTargetNotFound(true, toAddr, type);

		return to;
	}

}
