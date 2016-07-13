package bjoern.structures.interpretations;

import bjoern.nodeStore.NodeKey;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.annotations.VariableOrArgument;
import bjoern.structures.edges.DirectedEdge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

	public void registerBasicBlock(BasicBlock node) throws InvalidRadareFunctionException
	{
		BasicBlock block = getBasicBlockAtAddress(node.getAddress());

		if (block != null)
			throw new InvalidRadareFunctionException("Duplicate basic block in function");

		addBasicBlock(node.getAddress(), node);
	}

	public void addEdge(NodeKey sourceKey, NodeKey destKey, String type)
	{
		DirectedEdge newEdge = new DirectedEdge(sourceKey, destKey, type);
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


	public void updateInstructionsFromDisassembly()
	{
		for (BasicBlock block : getBasicBlocks())
		{
			for (Instruction instruction : block.getInstructions())
			{
				addDisassemblyProperties(instruction, this);
			}
		}
	}

	private static void addDisassemblyProperties(Instruction instruction, FunctionContent content)
	{
		if (content == null)
			return;
		DisassemblyLine line = content.getDisassemblyLineForAddr(instruction.getAddress());
		if (line == null)
			return;

		instruction.setComment(line.getComment());
		instruction.setStringRepr(line.getInstruction());
		DisassemblyLine esilLine = content.getDisassemblyEsilLineForAddr(instruction.getAddress());
		if (esilLine == null)
			return;

		instruction.setEsilCode(esilLine.getInstruction());
	}


}
