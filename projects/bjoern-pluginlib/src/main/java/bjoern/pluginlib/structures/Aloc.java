package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import com.tinkerpop.blueprints.Vertex;

public class Aloc extends BjoernNode
{
	public Aloc(Vertex vertex)
	{
		super(vertex, BjoernNodeTypes.ALOC);
	}

	public String getName()
	{
		return getProperty(BjoernNodeProperties.NAME).toString();
	}

	public boolean isFlag()
	{
		return getProperty(BjoernNodeProperties.SUBTYPE).toString().equals("flag");
	}

	public boolean isRegister()
	{
		return !isFlag();
	}
}
