package bjoern.pluginlib.structures;

import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;
import octopus.lib.structures.OctopusNode;

import com.tinkerpop.blueprints.Vertex;

public class Aloc extends OctopusNode
{
	public Aloc(Vertex vertex)
	{
		super(vertex, NodeTypes.ALOC);
	}

	public String getName()
	{
		return getProperty(BjoernNodeProperties.NAME).toString();
	}

	public boolean isFlag()
	{
		return getProperty(BjoernNodeProperties.SUBTYPE).toString().equals("flag");
	}
}
