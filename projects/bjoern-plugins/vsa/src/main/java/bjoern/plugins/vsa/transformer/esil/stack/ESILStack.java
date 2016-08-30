package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.domain.ValueSet;

import java.util.Deque;
import java.util.LinkedList;

public class ESILStack
{
	private final Deque<ESILStackItem<ValueSet>> stack;

	public ESILStack() {stack = new LinkedList<>();}

	public void push(ESILStackItem<ValueSet> item)
	{
		this.stack.push(item);
	}

	public ValueSet popValueSet()
	{
		return this.stack.pop().getValue();
	}

	public ESILStackItem<ValueSet> pop()
	{
		return stack.pop();
	}

	public ESILStackItem<ValueSet> peek()
	{
		return stack.peek();
	}
}
