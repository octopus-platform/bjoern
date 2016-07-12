package bjoern.structures.annotations;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;

import java.util.Map;

/**
 * A flag is a concept from radare2.
 * It's essentially an annotation attached to an address.
 * It has a fixed value and a length, that is, the flag
 * is associated with a sub string of the binary.
 */

public class Flag extends Node
{
	private String value;
	private long length;

	public Flag(long address)
	{
		super(address, NodeTypes.FLAG);
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

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.CODE, getValue());
		return properties;
	}
}
