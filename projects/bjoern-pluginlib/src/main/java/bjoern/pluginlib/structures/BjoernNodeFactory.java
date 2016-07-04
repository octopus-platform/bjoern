package bjoern.pluginlib.structures;

import bjoern.nodeStore.NodeTypes;
import bjoern.structures.BjoernNodeProperties;
import com.tinkerpop.blueprints.Vertex;

public class BjoernNodeFactory
{
	public static BjoernNode create(Vertex vertex)
	{
		String nodeType = vertex.getProperty(BjoernNodeProperties.TYPE);
		switch (nodeType)
		{
			case NodeTypes.ALOC:
				return new Aloc(vertex);
			case NodeTypes.BASIC_BLOCK:
				return new BasicBlock(vertex);
			case NodeTypes.FUNCTION:
				return new Function(vertex);
			case NodeTypes.INSTRUCTION:
				return new Instruction(vertex);
			default:
				return new BjoernNode(vertex);
		}

	}
}
