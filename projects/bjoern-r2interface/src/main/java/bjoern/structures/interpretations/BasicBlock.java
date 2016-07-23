package bjoern.structures.interpretations;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.Node;

import java.util.*;


public class BasicBlock extends Node
{

	HashMap<Long, Instruction> instructions = new HashMap<Long, Instruction>();
	List<Instruction> sortedInstructions = null;

	public BasicBlock(long address)
	{
		super(address, BjoernNodeTypes.BASIC_BLOCK);
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
		if (sortedInstructions != null)
			return;

		Collection<Instruction> collection = instructions.values();
		sortedInstructions = new ArrayList<>(collection);
		Collections.sort(sortedInstructions);
	}

	public String getInstructionsStr()
	{
		generateSortedInstructions();

		String retval = "";
		for (Instruction instr : sortedInstructions)
		{
			retval += instr.getStringRepr() + "|";
		}
		return retval;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getInstructionsStr());
		return properties;
	}

}
