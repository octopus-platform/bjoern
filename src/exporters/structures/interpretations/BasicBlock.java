package exporters.structures.interpretations;

import java.util.Collection;
import java.util.HashMap;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;


public class BasicBlock extends Node
{

	HashMap<Long, Instruction> instructions = new HashMap<Long, Instruction>();

	public BasicBlock()
	{
		this.setType(NodeTypes.BASIC_BLOCK);
	}

	public void addInstruction(Instruction instr)
	{
		instructions.put(instr.getAddress(), instr);
	}

	public Collection<Instruction> getInstructions()
	{
		return instructions.values();
	}

	public Instruction getInstructionAtAddress(long address)
	{
		return instructions.get(address);
	}

}
