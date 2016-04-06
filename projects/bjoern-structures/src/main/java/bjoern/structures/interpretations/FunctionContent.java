package bjoern.structures.interpretations;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import bjoern.nodeStore.NodeKey;
import bjoern.structures.annotations.VariableOrArgument;
import bjoern.structures.edges.DirectedEdge;

public class FunctionContent
{
	private final long functionAddr;
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<DirectedEdge> edges = new LinkedList<DirectedEdge>();
	DisassembledFunction disassembledFunction = new DisassembledFunction();
	private DisassembledFunction disassembledEsilFunction = new DisassembledFunction();

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

	public DisassemblyLine getDisassemblyEsilLineForAddr(long addr)
	{
		return disassembledEsilFunction.getLineForAddr(addr);
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


	public void setDisassembledFunction(DisassembledFunction func)
	{
		disassembledFunction = func;
	}

	public void setDisassembledEsilFunction(DisassembledFunction func)
	{
		disassembledEsilFunction = func;
	}

	
}
