package bjoern.structures.interpretations;

import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.edges.ControlFlowEdge;
import bjoern.structures.edges.DirectedEdge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FunctionContent
{
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<DirectedEdge> controlFlowEdges = new LinkedList<DirectedEdge>();

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<DirectedEdge> getControlFlowEdges()
	{
		return controlFlowEdges;
	}

	public BasicBlock getBasicBlockAtAddress(long addr)
	{
		return basicBlocks.get(addr);
	}

	public void addBasicBlock(long addr, BasicBlock node)
	{
		// TODO: It should be enough to pass node and
		// read its address from the respective field.
		basicBlocks.put(addr, node);
	}

	public void registerBasicBlock(BasicBlock node) throws InvalidRadareFunctionException
	{
		BasicBlock block = getBasicBlockAtAddress(node.getAddress());

		if (block != null)
			throw new InvalidRadareFunctionException("Duplicate basic block in function");

		addBasicBlock(node.getAddress(), node);
	}

	public void addControlFlowEdge(ControlFlowEdge edge)
	{
		controlFlowEdges.add(edge);
	}

}
