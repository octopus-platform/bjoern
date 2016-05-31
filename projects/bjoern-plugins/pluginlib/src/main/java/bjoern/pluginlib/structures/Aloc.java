package bjoern.pluginlib.structures;

import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;
import com.tinkerpop.blueprints.Vertex;

public class Aloc extends Node
{
	public Aloc(Vertex vertex)
	{
		super(vertex, NodeTypes.ALOC);
	}

	public String getName()
	{
		return getNode().getProperty(BjoernNodeProperties.NAME).toString();
	}

	public boolean isFlag()
	{
		return getNode().getProperty(BjoernNodeProperties.SUBTYPE).toString().equals("flag");
	}
}
