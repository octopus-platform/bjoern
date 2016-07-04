package bjoern.pluginlib.structures;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedElement;

public class BjoernEdge extends WrappedElement implements Edge
{
	public BjoernEdge(Edge edge)
	{
		super(edge);
	}

	@Override
	public Vertex getVertex(Direction direction) throws IllegalArgumentException
	{
		return BjoernNodeFactory.create(getBaseEdge().getVertex(direction));
	}

	@Override
	public String getLabel()
	{
		return getBaseEdge().getLabel();
	}

	private Edge getBaseEdge()
	{
		return (Edge) getBaseElement();
	}
}
