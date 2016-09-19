package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.domain.ValueSet;

import java.util.Deque;
import java.util.LinkedList;

public class ESILStack
{
	private final Deque<ESILStackItem> stack;

	public ESILStack() {stack = new LinkedList<>();}

	public void push(ESILStackItem item)
	{
		this.stack.push(item);
	}

	public void pushValueSet(ValueSet valueSet)
	{
		this.push(new ValueSetContainer(valueSet));
	}

	public ValueSet popValueSet()
	{
		return this.stack.pop().getValue();
	}

	public ESILStackItem pop()
	{
		return stack.pop();
	}

	public ESILStackItem peek()
	{
		return stack.peek();
	}

	@Override
	public String toString()
	{
		return stack.toString();
	}

}
