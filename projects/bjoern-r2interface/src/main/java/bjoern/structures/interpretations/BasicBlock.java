package bjoern.structures.interpretations;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.Node;

import java.util.*;
import java.util.stream.Collectors;


public class BasicBlock extends Node
{
	private final Map<Long, Instruction> instructions;
	private List<Instruction> sortedInstructions = null;

	public BasicBlock(Builder builder)
	{
		super(builder);
		this.instructions = builder.addressToInstruction;
	}

	public static class Builder extends Node.Builder
	{

		private Map<Long, Instruction> addressToInstruction = new HashMap<>();

		public Builder(Long address)
		{
			super(address, BjoernNodeTypes.BASIC_BLOCK);
		}

		public Builder withInstructions(List<Instruction> instructions)
		{
			addressToInstruction = instructions.stream()
					.collect(Collectors.toMap(Instruction::getAddress, instruction -> instruction));
			return this;
		}

		public BasicBlock build()
		{
			return new BasicBlock(this);
		}
	}

	public List<Instruction> getInstructions()
	{
		return getSortedInstructions();
	}

	private List<Instruction> getSortedInstructions()
	{
		if (sortedInstructions != null)
			return sortedInstructions;

		Collection<Instruction> collection = instructions.values();
		sortedInstructions = new ArrayList<>(collection);
		Collections.sort(sortedInstructions);
		return sortedInstructions;
	}

	public String getInstructionsStr()
	{
		final String DELIMITER = " | ";
		StringBuilder stringBuilder = new StringBuilder();
		for (Instruction instr : getSortedInstructions())
		{
			stringBuilder.append(instr.getStringRepr());
			stringBuilder.append(DELIMITER);
		}
		if (stringBuilder.length() > DELIMITER.length())
		{
			stringBuilder.setLength(stringBuilder.length() - DELIMITER.length());
		}
		return stringBuilder.toString();
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getInstructionsStr());
		return properties;
	}

}
