package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public interface ESILCommand
{
	ESILStackItem execute(Deque<ESILCommand> stack);
}
