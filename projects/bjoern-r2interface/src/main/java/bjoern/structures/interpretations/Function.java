package bjoern.structures.interpretations;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.Node;

import java.util.Map;


public class Function extends Node
{

	private final FunctionContent content;
	private final String name;

	public static class Builder extends Node.Builder
	{

		private String name;
		private FunctionContent content;

		public Builder(Long address)
		{
			super(address, BjoernNodeTypes.FUNCTION);
		}

		public Builder withName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder withContent(FunctionContent content)
		{
			this.content = content;
			return this;
		}

		public Function build()
		{
			return new Function(this);
		}

	}

	public Function(Builder builder)
	{
		super(builder);
		this.name = builder.name;
		this.content = builder.content;
	}

	public FunctionContent getContent()
	{
		return content;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getName());
		return properties;
	}
}
