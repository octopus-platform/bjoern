package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.BjoernNodeProperties;
import com.tinkerpop.blueprints.Vertex;

public class BjoernNodeFactory
{
	public static BjoernNode create(Vertex vertex)
	{
		String nodeType = vertex.getProperty(BjoernNodeProperties.TYPE);
		switch (nodeType)
		{
			case BjoernNodeTypes.ALOC:
				return new Aloc(vertex);
			case BjoernNodeTypes.BASIC_BLOCK:
				return new BasicBlock(vertex);
			case BjoernNodeTypes.FUNCTION:
				return new Function(vertex);
			case BjoernNodeTypes.INSTRUCTION:
				return new Instruction(vertex);
			default:
				return new BjoernNode(vertex);
		}

	}
}
