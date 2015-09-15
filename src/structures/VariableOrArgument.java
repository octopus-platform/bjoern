package structures;

import nodeStore.Node;

public class VariableOrArgument extends Node
{
	private String type;
	private String varType;
	private String varName;
	private String regPlusOffset;

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

}
