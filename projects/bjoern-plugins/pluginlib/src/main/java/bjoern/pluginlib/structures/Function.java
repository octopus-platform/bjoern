package bjoern.pluginlib.structures;

import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import bjoern.structures.edges.EdgeTypes;

public class Function extends Node
{

	private List<BasicBlock> blocks;

	public Function(Vertex vertex)
	{
		super(vertex);
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
