package exporters.structures.interpretations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

	public List<Instruction> getInstructions()
	{
		Collection<Instruction> collection = instructions.values();
		List<Instruction> retval = new ArrayList<Instruction>(collection);
		Comparator<? super Instruction> c;

		retval.sort(new InstructionComparator());
		return retval;
	}

	public Instruction getInstructionAtAddress(long address)
	{
		return instructions.get(address);
	}

}

class InstructionComparator implements Comparator<Instruction>{

	@Override
	public int compare(Instruction o1, Instruction o2) {
		return o1.getAddress().compareTo(o2.getAddress());
	}

}
