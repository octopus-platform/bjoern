package bjoern.pluginlib.structures;

import com.tinkerpop.blueprints.Edge;

import java.util.Iterator;

public class BjoernEdgeIterable implements Iterable<Edge>
{
	private final Iterable<Edge> iterable;

	public BjoernEdgeIterable(Iterable<Edge> iterable)
	{
		this.iterable = iterable;
	}

	@Override
	public Iterator<Edge> iterator()
	{
		return new Iterator<Edge>()
		{
			final Iterator<Edge> baseIterable = iterable.iterator();

			@Override
			public boolean hasNext()
			{
				return baseIterable.hasNext();
			}

			@Override
			public Edge next()
			{
				return new BjoernEdge(baseIterable.next());
			}
		};
	}
}
