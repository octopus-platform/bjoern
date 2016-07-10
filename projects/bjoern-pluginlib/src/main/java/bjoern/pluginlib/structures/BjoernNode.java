package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeProperties;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.wrappers.WrapperVertexQuery;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import octopus.lib.structures.OctopusNode;

public class BjoernNode extends OctopusNode
{

	public BjoernNode(Vertex vertex)
	{
		super(vertex);
	}

	public BjoernNode(Vertex vertex, String nodeType)
	{
		super(vertex, nodeType);
	}

	@Override
	public Iterable<Edge> getEdges(Direction direction, String... labels)
	{
		return new BjoernEdgeIterable(getBaseVertex().getEdges(direction, labels));
	}

	@Override
	public Iterable<Vertex> getVertices(Direction direction, String... labels)
	{
		return new BjoernVertexIterable(getBaseVertex().getVertices(direction, labels));
	}

	@Override
	public VertexQuery query()
	{
		return new WrapperVertexQuery(getBaseVertex().query())
		{
			@Override
			public Iterable<Edge> edges()
			{
				return new BjoernEdgeIterable(query.edges());
			}

			@Override
			public Iterable<Vertex> vertices()
			{
				return new BjoernVertexIterable(query.vertices());
			}
		};
	}

	@Override
	public Edge addEdge(String label, Vertex inVertex)
	{
		if (inVertex instanceof BjoernNode)
		{
			return new BjoernEdge(getBaseVertex().addEdge(label, ((BjoernNode) inVertex).getBaseVertex()));
		} else
		{
			return new BjoernEdge(getBaseVertex().addEdge(label, inVertex));
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getProperty(BjoernNodeProperties.TYPE).toString());
		sb.append("(");
		sb.append(BjoernNodeProperties.REPR);
		sb.append(":");
		sb.append(getProperty(BjoernNodeProperties.REPR).toString());
		sb.append(", ");
		sb.append(BjoernNodeProperties.ADDR);
		sb.append(":");
		sb.append(getProperty(BjoernNodeProperties.ADDR).toString());
		sb.append(")");
		return sb.toString();
	}

	public GremlinPipeline<BjoernNode, BjoernNode> start()
	{
		return new GremlinPipeline<>(this);
	}

	public Long getAddress()
	{
		return Long.parseLong(getProperty(BjoernNodeProperties.ADDR).toString(), 16);
	}

	public String getRepresentation()
	{
		return getProperty(BjoernNodeProperties.REPR).toString();
	}
}
