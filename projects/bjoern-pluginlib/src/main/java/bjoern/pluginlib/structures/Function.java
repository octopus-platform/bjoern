package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class Function extends BjoernNode
{

	public Function(Vertex vertex)
	{
		super(vertex, BjoernNodeTypes.FUNCTION);
	}

	public GremlinPipeline<?, BasicBlock> basicBlocks()
	{
		return new GremlinPipeline<>().start(this.getBaseVertex()).out(EdgeTypes.IS_FUNCTION_OF).transform(BasicBlock::new);
	}

}
