package bjoern.structures.interpretations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;


public class BasicBlock extends Node
{

	HashMap<Long, Instruction> instructions = new HashMap<Long, Instruction>();
	List<Instruction> sortedInstructions = null;

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
		generateSortedInstructions();
		return sortedInstructions;
	}

	private void generateSortedInstructions()
	{
		if(sortedInstructions != null)
			return;

		Collection<Instruction> collection = instructions.values();
		sortedInstructions = new ArrayList<Instruction>(collection);
		Comparator<? super Instruction> c;

		sortedInstructions.sort(new InstructionComparator());
	}

	public Instruction getInstructionAtAddress(long address)
	{
		return instructions.get(address);
	}

	public String getInstructionsStr()
	{
		generateSortedInstructions();

		String retval = "";
		for(Instruction instr : sortedInstructions)
		{
			retval += instr.getStringRepr() + "|";
		}
		return retval;
	}

}

class InstructionComparator implements Comparator<Instruction>{

	@Override
	public int compare(Instruction o1, Instruction o2) {
		return o1.getAddress().compareTo(o2.getAddress());
	}

}
