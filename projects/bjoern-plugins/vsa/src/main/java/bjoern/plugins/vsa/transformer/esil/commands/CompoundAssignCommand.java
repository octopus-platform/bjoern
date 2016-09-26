package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class CompoundAssignCommand implements ESILCommand
{
	private final ESILCommand command;

	public CompoundAssignCommand(ESILCommand command)
	{
		this.command = command;
	}

	@Override
	public final ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ESILCommand item = stack.peek();
		stack.push(command);
		stack.push(item);
		return ESILCommandFactory.getCommand(ESILKeyword.ASSIGNMENT)
				.execute(stack);
	}
}
