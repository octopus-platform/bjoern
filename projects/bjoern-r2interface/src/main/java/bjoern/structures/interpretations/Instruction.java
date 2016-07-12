package bjoern.structures.interpretations;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;

import java.util.Map;

public class Instruction extends Node
{
	private String stringRepr;
	private String esilCode;
	private String bytes;

	public Instruction(long address)
	{
		super(address, NodeTypes.INSTRUCTION);
	}

	public String getStringRepr()
	{
		return stringRepr;
	}

	public void setStringRepr(String stringRepr)
	{
		this.stringRepr = stringRepr;
	}

	public String getBytes()
	{
		return this.bytes;
	}

	public void setBytes(String bytes)
	{
		this.bytes = bytes;
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

	public void setEsilCode(String esilCode)
	{
		this.esilCode = esilCode;
	}
}
