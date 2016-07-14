package bjoern.structures.interpretations;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;

import java.util.Map;


public class Function extends Node
{

	FunctionContent content;

	private String name = "";

	public Function(long addr)
	{
		super(addr, NodeTypes.FUNCTION);
		content = new FunctionContent();
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

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getName());
		return properties;
	}
}
