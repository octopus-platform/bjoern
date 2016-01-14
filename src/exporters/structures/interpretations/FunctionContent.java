package exporters.structures.interpretations;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exporters.nodeStore.NodeKey;
import exporters.radare.inputModule.RadareDisassemblyParser;
import exporters.radare.inputModule.exceptions.InvalidDisassembly;
import exporters.structures.annotations.VariableOrArgument;
import exporters.structures.edges.DirectedEdge;

public class FunctionContent
{
	private final long functionAddr;
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<DirectedEdge> edges = new LinkedList<DirectedEdge>();
	DisassembledFunction disassembledFunction = new DisassembledFunction();

	public FunctionContent(long functionAddr)
	{
		this.functionAddr = functionAddr;
	}

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<DirectedEdge> getEdges()
	{
		return edges;
	}

	public BasicBlock getBasicBlockAtAddress(long addr)
	{
		return basicBlocks.get(addr);
	}

	public DisassemblyLine getDisassemblyLineForAddr(long addr)
	{
		return disassembledFunction.getLineForAddr(addr);
	}

	public List<VariableOrArgument> getVariablesAndArguments()
	{
		return disassembledFunction.getVariablesAndArguments();
	}

	public void addBasicBlock(long addr, BasicBlock node)
	{
		// TODO: It should be enough to pass node and
		// read its address from the respective field.
		basicBlocks.put(addr, node);
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		BasicBlock block = getBasicBlockAtAddress(addr);

		if (block != null)
			throw new RuntimeException("Duplicate Node");

		addBasicBlock(addr, node);
	}

	public void addEdge(NodeKey sourceKey, NodeKey destKey, String type)
	{
		DirectedEdge newEdge = new DirectedEdge();

		newEdge.setSourceKey(sourceKey);
		newEdge.setDestKey(destKey);
		newEdge.setType(type);
		edges.add(newEdge);

	}


	public void consumeDisassembly(String disassemblyStr)
	{
		RadareDisassemblyParser parser = new RadareDisassemblyParser();
		try
		{
			disassembledFunction = parser.parseFunction(disassemblyStr, functionAddr);
		}
		catch (InvalidDisassembly e)
		{
			// TODO: might want to log this error.
		}
	}

}
