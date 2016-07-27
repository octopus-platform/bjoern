package bjoern.structures;

import java.util.HashMap;
import java.util.Map;

public abstract class Node
{
	private NodeKey nodeKey;
	private final Long address;
	private final String type;
	private final String comment;

	public static abstract class Builder
	{
		private final Long address;
		private final String type;
		private String comment;

		public Builder(Long address, String type)
		{
			this.address = address;
			this.type = type;
		}

		public Builder withComment(String comment)
		{
			this.comment = comment;
			return this;
		}
	}

	public Node(Builder builder)
	{
		this.address = builder.address;
		this.type = builder.type;
		this.comment = builder.comment;
	}

	public Long getAddress()
	{
		return address;
	}

	public String getAddressAsHexString()
	{
		return Long.toHexString(getAddress());
	}

	public String getType()
	{
		return type;
	}

	public String getComment()
	{
		return comment;
	}

	public NodeKey createKey()
	{
		if (nodeKey == null)
		{
			nodeKey = new NodeKey(getAddress(), getType());
		}
		return nodeKey;
	}

	public String getKey()
	{
		return getType() + "_" + getAddressAsHexString();
	}

	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = new HashMap<>();
		properties.put(BjoernNodeProperties.KEY, getKey());
		properties.put(BjoernNodeProperties.TYPE, getType());
		properties.put(BjoernNodeProperties.ADDR, getAddressAsHexString());
		properties.put(BjoernNodeProperties.COMMENT, getComment());
		return properties;
	}

	@Override
	public String toString()
	{
		return getKey();
	}
}
