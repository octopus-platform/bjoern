package bjoern.pluginlib.structures;

import com.tinkerpop.blueprints.Vertex;

import java.util.Iterator;

public class BjoernVertexIterable implements Iterable<Vertex>
{
	private final Iterable<Vertex> iterable;

	public BjoernVertexIterable(Iterable<Vertex> iterable)
	{
		this.iterable = iterable;
	}

	@Override
	public Iterator<Vertex> iterator()
	{
		return new Iterator<Vertex>()
		{
			final Iterator<Vertex> baseIterator = iterable.iterator();

			@Override
			public boolean hasNext()
			{
				return baseIterator.hasNext();
			}

			@Override
			public Vertex next()
			{
				return BjoernNodeFactory.create(baseIterator.next());
			}
		};
	}
}
