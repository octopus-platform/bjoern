package bjoern.pluginlib.structures;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.util.StringFactory;
import com.tinkerpop.blueprints.util.wrappers.WrappedGraphQuery;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedEdge;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;

public class BjoernGraph<T extends Graph> implements Graph, WrapperGraph<T>
{

	protected T baseGraph;
	private final Features features;

	public BjoernGraph(final T baseGraph)
	{
		this.baseGraph = baseGraph;
		this.features = this.baseGraph.getFeatures().copyFeatures();
		this.features.isWrapper = true;
	}

	public void shutdown()
	{
		this.baseGraph.shutdown();
	}

	public Vertex addVertex(final Object id)
	{
		return new BjoernNode(this.baseGraph.addVertex(id));
	}

	public Vertex getVertex(final Object id)
	{
		final Vertex vertex = this.baseGraph.getVertex(id);
		if (null == vertex)
		{
			return null;
		} else
		{
			return BjoernNodeFactory.create(vertex);
		}
	}

	public Iterable<Vertex> getVertices()
	{
		return new BjoernVertexIterable(this.baseGraph.getVertices());
	}

	public Iterable<Vertex> getVertices(final String key, final Object value)
	{
		return new BjoernVertexIterable(this.baseGraph.getVertices(key, value));
	}

	public Edge addEdge(final Object id, final Vertex outVertex, final Vertex inVertex, final String label)
	{
		return new BjoernEdge(this.baseGraph.addEdge(id, ((BjoernNode) outVertex).getBaseVertex(), (
				(BjoernNode) inVertex).getBaseVertex(), label));
	}

	public Edge getEdge(final Object id)
	{
		final Edge edge = this.baseGraph.getEdge(id);
		if (null == edge)
			return null;
		else
			return new BjoernEdge(edge);
	}

	public Iterable<Edge> getEdges()
	{
		return new BjoernEdgeIterable(this.baseGraph.getEdges());
	}

	public Iterable<Edge> getEdges(final String key, final Object value)
	{
		return new BjoernEdgeIterable(this.baseGraph.getEdges(key, value));
	}

	public void removeEdge(final Edge edge)
	{
		this.baseGraph.removeEdge(((WrappedEdge) edge).getBaseEdge());
	}

	public void removeVertex(final Vertex vertex)
	{
		this.baseGraph.removeVertex(((WrappedVertex) vertex).getBaseVertex());
	}

	@Override
	public T getBaseGraph()
	{
		return this.baseGraph;
	}

	public GraphQuery query()
	{
		return new WrappedGraphQuery(this.baseGraph.query())
		{
			@Override
			public Iterable<Edge> edges()
			{
				return new BjoernEdgeIterable(this.query.edges());
			}

			@Override
			public Iterable<Vertex> vertices()
			{
				return new BjoernVertexIterable(this.query.vertices());
			}
		};
	}

	public String toString()
	{
		return StringFactory.graphString(this, this.baseGraph.toString());
	}

	public Features getFeatures()
	{
		return this.features;
	}
}