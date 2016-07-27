package bjoern.structures.annotations;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.Node;

import java.util.Map;

/**
 * A flag is a concept from radare2.
 * It's essentially an annotation attached to an address.
 * It has a fixed value and a length, that is, the flag
 * is associated with a sub string of the binary.
 */

public class Flag extends Node
{
	private final String value;
	private final long length;

	public static class Builder extends Node.Builder
	{
		private String value;
		private long length;

		public Builder(Long address)
		{
			super(address, BjoernNodeTypes.FLAG);
		}

		public Builder withValue(String value)
		{
			this.value = value;
			return this;
		}

		public Builder withLenght(long length)
		{
			this.length = length;
			return this;
		}

		public Flag build()
		{
			return new Flag(this);
		}
	}

	public Flag(Builder builder)
	{
		super(builder);
		this.value = builder.value;
		this.length = builder.length;
	}

	public String getValue()
	{
		return value;
	}

	public long getLength()
	{
		return length;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.CODE, getValue());
		return properties;
	}
}
