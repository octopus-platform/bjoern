package bjoern.structures.annotations;

import bjoern.nodeStore.Node;
import bjoern.structures.BjoernNodeProperties;

import java.util.Map;

public class VariableOrArgument extends Node
{
	private String type;
	private String varType;
	private String varName;
	private String regPlusOffset;

	public VariableOrArgument(long address)
	{
		super(address, "");
	}

	public void setType(String varOrArg)
	{
		this.type = varOrArg;
	}

	public void setVarType(String varType)
	{
		this.varType = varType;
	}

	public void setName(String varName)
	{
		this.varName = varName;
	}

	public void setRegPlusOffset(String regPlusOffset)
	{
		this.regPlusOffset = regPlusOffset;
	}

	public String getType()
	{
		return type;
	}

	public String getVarType()
	{
		return varType;
	}

	public String getVarName()
	{
		return varName;
	}

	public String getRegPlusOffset()
	{
		return regPlusOffset;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(BjoernNodeProperties.REPR, getVarType());
		properties.put(BjoernNodeProperties.NAME, getVarName());
		properties.put(BjoernNodeProperties.CODE, getRegPlusOffset());
		return properties;
	}
}
