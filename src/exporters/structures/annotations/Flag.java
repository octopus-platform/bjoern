package exporters.structures.annotations;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;

public class Flag extends Node
{
	private String value;
	private long length;

	public Flag()
	{
		this.setType(NodeTypes.FLAG);
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(long length)
	{
		this.length = length;
	}
}
