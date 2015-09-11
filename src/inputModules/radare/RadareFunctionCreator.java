package inputModules.radare;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptions.radareInput.BasicBlockWithoutAddress;
import exceptions.radareInput.EdgeTargetNotFound;
import nodeStore.NodeStore;
import nodeStore.NodeTypes;
import structures.BasicBlock;
import structures.CFGEdgeType;
import structures.Function;

public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Function function = new Function();
		initFromJSON(function, jsonFunction);
		return function;
	}

	public static void initFromJSON(Function function, JSONObject jsonFunction)
	{
		initFunctionProperties(function, jsonFunction);
		createBasicBlocks(function, jsonFunction);
		createEdges(function, jsonFunction);
	}

	private static void initFunctionProperties(Function function,
			JSONObject jsonFunction)
	{
		String name = JSONUtils.getStringFromObject(jsonFunction, "name");
		if (name != null)
			function.setName(name);
	}

	private static void createBasicBlocks(Function function,
			JSONObject jsonFunction)
	{
		JSONArray blocks = jsonFunction.getJSONArray("blocks");
		int numberOfBlocks = blocks.length();

		for (int i = 0; i < numberOfBlocks; i++)
		{
			JSONObject block = blocks.getJSONObject(i);
			try
			{
				createBasicBlock(function, block);
			} catch (BasicBlockWithoutAddress e)
			{
				System.err.println("Skipping basic block without address");
				continue;
			}
		}
	}

	private static void createBasicBlock(Function function,
			JSONObject jsonBlock) throws BasicBlockWithoutAddress
	{

		Long address = JSONUtils.getLongFromObject(jsonBlock, "offset");
		if (address == null)
			throw new BasicBlockWithoutAddress();

		BasicBlock node = createBlockOrTakeExisting(jsonBlock, address);
		function.registerBasicBlock(address, node);

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

	private static void createEdges(Function function, JSONObject jsonFunction)
	{
		JSONArray blocks = jsonFunction.getJSONArray("blocks");
		int numberOfBlocks = blocks.length();
		for (int i = 0; i < numberOfBlocks; i++)
		{
			JSONObject jsonBlock = blocks.getJSONObject(i);
			try
			{
				createEdgesForBlock(function, jsonBlock);
			} catch (EdgeTargetNotFound e)
			{
				// If target wasn't even given, we're fine.
				if (!e.isTargetGiven())
					continue;
				// Otherwise, the target was given but we were unable to resolve
				// it. Store target address, we might be able to resolve it
				// later.
				BasicBlock basicBlock = getBasicBlockForJSONBlock(jsonBlock);
				function.addUnresolvedEdge(basicBlock.getAddress(),
						e.getAddress());
			}
		}
	}

	private static void createEdgesForBlock(Function function,
			JSONObject jsonBlock) throws EdgeTargetNotFound
	{
		int numberOfEdges = 2;

		BasicBlock fromBlock = getBasicBlockForJSONBlock(jsonBlock);
		BasicBlock jumpBlock = getJumpTarget(jsonBlock, "jump");
		BasicBlock failBlock = null;

		try
		{
			failBlock = getJumpTarget(jsonBlock, "fail");
		} catch (EdgeTargetNotFound ex)
		{
			numberOfEdges = 1;
		}

		if (numberOfEdges == 1)
			function.addEdge(fromBlock, jumpBlock, CFGEdgeType.UNCONDITIONAL);
		else
		{
			assert(failBlock != null);
			function.addEdge(fromBlock, jumpBlock, CFGEdgeType.TRUE);
			function.addEdge(fromBlock, failBlock, CFGEdgeType.FALSE);
		}

	}

	private static BasicBlock getBasicBlockForJSONBlock(JSONObject block)
	{
		Long blockAddr = JSONUtils.getLongFromObject(block, "offset");
		assert(blockAddr != null);

		BasicBlock fromBlock = (BasicBlock) NodeStore
				.getNodeForAddressAndType(blockAddr, NodeTypes.BASIC_BLOCK);

		if (fromBlock == null)
			throw new RuntimeException("From-node not in store.");

		return fromBlock;
	}

	private static BasicBlock getJumpTarget(JSONObject block, String type)
			throws EdgeTargetNotFound
	{
		Long toAddr = JSONUtils.getLongFromObject(block, type);
		if (toAddr == null)
			throw new EdgeTargetNotFound(false, 0);

		BasicBlock to = (BasicBlock) NodeStore.getNodeForAddressAndType(toAddr,
				NodeTypes.BASIC_BLOCK);
		if (to == null)
			throw new EdgeTargetNotFound(true, toAddr);

		return to;
	}

}
