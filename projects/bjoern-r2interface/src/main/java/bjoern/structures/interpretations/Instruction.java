package bjoern.structures.interpretations;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.Node;

import java.util.Map;

public class Instruction extends Node implements Comparable<Instruction>
{
	private final String stringRepr;
	private final String esilCode;
	private final String bytes;

	public static class Builder extends Node.Builder
	{

		private String representation;
		private String esilCode;
		private String bytes;

		public Builder(Long address)
		{
			super(address, BjoernNodeTypes.INSTRUCTION);
		}

		public Builder withRepresentation(String representation)
		{
			this.representation = representation;
			return this;
		}

		public Builder withESILCode(String esilCode)
		{
			this.esilCode = esilCode;
			return this;
		}

		public Builder withBytes(String bytes)
		{
			this.bytes = bytes;
			return this;
		}

		@Override
		public Builder withComment(String comment)
		{
			return (Builder) super.withComment(comment);
		}

		public Instruction build()
		{
			return new Instruction(this);
		}
	}

	public Instruction(Builder builder)
	{
		super(builder);
		this.stringRepr = builder.representation;
		this.esilCode = builder.esilCode;
		this.bytes = builder.bytes;
	}

	public String getStringRepr()
	{
		return stringRepr;
	}

	public String getBytes()
	{
		return this.bytes;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getStringRepr());
		properties.put(BjoernNodeProperties.CODE, getBytes());
		properties.put(BjoernNodeProperties.ESIL, getEsilCode());
		return properties;
	}

	public String getEsilCode()
	{
		return esilCode;
	}

	@Override
	public int compareTo(Instruction o)
	{
		if (this.getAddress() > o.getAddress())
		{
			return 1;
		} else if (this.getAddress() < o.getAddress())
		{
			return -1;
		} else
		{
			return 0;
		}
	}
}
