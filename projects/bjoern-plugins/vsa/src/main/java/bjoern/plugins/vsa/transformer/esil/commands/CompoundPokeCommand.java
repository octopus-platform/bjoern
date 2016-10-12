package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class CompoundPokeCommand implements ESILCommand {

	private final ESILCommand pokeCommand;
	private final ESILCommand command;

	public CompoundPokeCommand(ESILCommand command, ESILCommand pokeCommand) {
		this.command = command;
		this.pokeCommand = pokeCommand;
	}

	@Override
	public ESILStackItem execute(
			final Deque<ESILCommand> stack, final AbstractEnvironment env) {

		ESILStackItem address = stack.pop().execute(stack, env);
		stack.push((stack1, env1) -> address);
		stack.push(command);
		stack.push((stack1, env1) -> address);
		return pokeCommand.execute(stack, env);
	}
}
