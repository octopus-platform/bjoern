package bjoern.pluginlib;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;

public class Traversals {

	public static BasicBlock functionToEntryBlock(Vertex func)
	{
		GremlinPipeline<Vertex,Vertex> pipe = new GremlinPipeline<Vertex,Vertex>();

		pipe.start(func).in(EdgeTypes.INTERPRETATION)
		.out(EdgeTypes.INTERPRETATION).filter(new PipeFunction<Vertex,Boolean>() {
				@Override
				public Boolean compute(Vertex v) {
					return v.getProperty(BjoernNodeProperties.TYPE).equals(NodeTypes.BASIC_BLOCK);
				  }
			}
		);

		if(!pipe.hasNext())
			return null;

		Vertex vertex = pipe.next();
		if(vertex == null)
			return null;

		return new BasicBlock(vertex);
	}

}
