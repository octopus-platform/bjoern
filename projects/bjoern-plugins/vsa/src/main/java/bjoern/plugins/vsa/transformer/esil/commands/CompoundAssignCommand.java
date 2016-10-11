package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class CompoundAssignCommand implements ESILCommand {
	private final ESILCommand command;
	private final ESILCommand assignCommand;

	public CompoundAssignCommand(
			ESILCommand command, ESILCommand assignCommand) {
		this.command = command;
		this.assignCommand = assignCommand;
	}

	@Override
	public final ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ESILCommand item = stack.peek();
		stack.push(command);
		stack.push(item);
		return assignCommand.execute(stack, env);
	}
}
