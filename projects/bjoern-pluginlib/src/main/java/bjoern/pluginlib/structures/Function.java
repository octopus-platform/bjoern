package bjoern.pluginlib.structures;

import java.util.ArrayList;
import java.util.List;

import bjoern.nodeStore.NodeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import bjoern.structures.edges.EdgeTypes;
import octopus.lib.structures.OctopusNode;

public class Function extends OctopusNode
{

	private List<BasicBlock> blocks;

	public Function(Vertex vertex)
	{
		super(vertex, NodeTypes.FUNCTION);
	}

	public List<BasicBlock> getBasicBlocks()
	{
		if (blocks == null)
		{
			blocks = new ArrayList<BasicBlock>();
			for (Vertex block : getNode().getVertices(Direction.OUT,
					EdgeTypes.IS_FUNCTION_OF))
			{
				blocks.add(new BasicBlock(block));
			}
		}
		return blocks;
	}
}
