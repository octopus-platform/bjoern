package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

public class ObservableESILCommand implements ESILCommand
{
	private final ESILCommand command;
	private Collection<ESILCommandObserver> observers;

	public ObservableESILCommand(ESILCommand command)
	{
		this.command = command;
		this.observers = new ArrayList<>();
	}

	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		notifyBeforeExecution();
		ESILStackItem result = this.command.execute(stack);
		notifyAfterExecution();
		return result;
	}

	private void notifyBeforeExecution()
	{
		for (ESILCommandObserver observer : observers)
		{
			observer.beforeExecution(this.command);
		}
	}

	private void notifyAfterExecution()
	{
		for (ESILCommandObserver observer : observers)
		{
			observer.afterExecution(this.command);
		}
	}

	public void addObserver(ESILCommandObserver observer)
	{
		this.observers.add(observer);
	}
}
