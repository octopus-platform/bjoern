package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class PopCommand implements ESILCommand
{
	private ESILStackItem item;

	public PopCommand(ESILStackItem item)
	{
		this.item = item;
	}

	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		return this.item;
	}
}
