package bjoern.structures.interpretations;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;


public class Function extends Node
{

	FunctionContent content;

	private String name = "";

	public Function(long addr)
	{
		super(addr, NodeTypes.FUNCTION);
		content = new FunctionContent(addr);
	}

	public FunctionContent getContent()
	{
		return content;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setContent(FunctionContent content)
	{
		this.content = content;
	}

	public void deleteContent()
	{
		content = null;
	}

}
