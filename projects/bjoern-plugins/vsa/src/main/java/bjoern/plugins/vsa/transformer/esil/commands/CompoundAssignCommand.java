package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

public class CompoundAssignCommand implements ESILCommand
{
	private final ESILCommand command;

	public CompoundAssignCommand(ESILCommand command)
	{
		this.command = command;
	}

	@Override
	public final void execute(AbstractEnvironment env, ESILStack stack)
	{
		ESILStackItem item = stack.peek();
		command.execute(env, stack);
		stack.push(item);
		ESILCommandFactory.getCommand(ESILKeyword.ASSIGNMENT).execute(env, stack);
	}
}
