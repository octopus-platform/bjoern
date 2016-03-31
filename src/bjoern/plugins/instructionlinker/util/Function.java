package bjoern.plugins.instructionlinker.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import java.util.ArrayList;
import java.util.List;

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
					"IS_FUNC_OF"))
			{
				blocks.add(new BasicBlock(block));
			}
		}
		return blocks;
	}
}
