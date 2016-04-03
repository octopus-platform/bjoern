package bjoern.plugins.instructionlinker.util;

import com.tinkerpop.blueprints.Vertex;

public class Instruction extends Node implements Comparable<Instruction>
{

	public Instruction(Vertex vertex)
	{
		super(vertex);
	}

	public long getAddress()
	{
		return Long.parseLong(getNode().getProperty("addr").toString());
	}

	@Override
	public int compareTo(Instruction instruction)
	{
		if (this.getAddress() < instruction.getAddress())
		{
			return -1;
		} else if (this.getAddress() > instruction.getAddress())
		{
			return 1;
		} else
		{
			return 0;
		}
	}
}
