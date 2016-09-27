package bjoern.plugins.datadependence;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.*;
import java.util.function.Function;

class ReachingDefinitionAnalyser
{
	private static final String[] CFLOW_LABEL = {"NEXT_INSTR",
			"NEXT_INSTR_TRANSITIVE"};
	private final Function<Vertex, Set<Definition>> gen;
	private final Function<Vertex, Set<Definition>> kill;
	private Map<Vertex, Set<Definition>> out;

	static class Definition
	{
		private final Vertex location;
		private final Object identifier;

		Definition(Vertex location, Object identifier)
		{
			this.location = location;
			this.identifier = identifier;
		}

		public Vertex getLocation()
		{
			return location;
		}

		public Object getIdentifier()
		{
			return identifier;
		}

		@Override
		public boolean equals(Object object)
		{
			if (!(object instanceof Definition))
			{
				return false;
			}
			Definition definition = (Definition) object;
			return location.equals(definition.location)
					&& identifier.equals(definition.identifier);
		}

		@Override
		public int hashCode()
		{
			int hashCode = 17;
			hashCode = 31 * hashCode + location.hashCode();
			hashCode = 31 * hashCode + identifier.hashCode();
			return hashCode;
		}

		@Override
		public String toString()
		{
			return identifier.toString() + "@" + location.getId().toString();
		}
	}

	ReachingDefinitionAnalyser()
	{
		this.gen = vertex ->
		{
			Set<Definition> genSet = new HashSet<>();
			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
			pipe.start(vertex).out("WRITE").out("BELONGS_TO")
					.in("BELONGS_TO");
			for (Vertex register : pipe)
			{
				String registerName = register.getProperty("identifier");
				genSet.add(new Definition(vertex, registerName));
			}
			return genSet;
		};
		this.kill = vertex ->
		{
			Set<Definition> killSet = new HashSet<>();
			GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<>();
			pipe.start(vertex).out("WRITE").out("BELONGS_TO").in("BELOGNS_TO")
					.inE("WRITE");
			for (Edge writeEdge : pipe)
			{
				String registerName = writeEdge.getVertex(Direction.IN)
						.getProperty("identifier");
				Vertex genVertex = writeEdge.getVertex(Direction.OUT);
				killSet.add(new Definition(genVertex, registerName));
			}
			return killSet;
		};
	}

	Map<Vertex, Set<Definition>> analyse(Vertex entry)
	{
		this.out = new HashMap<>();
		Queue<Vertex> worklist = getAllNodes(entry);
		while (!worklist.isEmpty())
		{
			Vertex vertex = worklist.remove();
			Set<Definition> in = getIn(vertex);
			Set<Definition> outNew = calculateOut(vertex, in);
			Set<Definition> outOld = getOut(vertex);
			if (!outNew.equals(outOld))
			{
				this.out.put(vertex, outNew);
				for (Vertex successor : vertex.getVertices(Direction.OUT,
						CFLOW_LABEL))
				{
					if (!worklist.contains(successor))
					{
						worklist.add(successor);
					}
				}
			}
		}
		return collectReachingDefinitions();
	}

	private Map<Vertex, Set<Definition>> collectReachingDefinitions()
	{
		Map<Vertex, Set<Definition>> reachingDefinitions = new HashMap<>();
		for (Vertex vertex : out.keySet())
		{
			Set<Definition> definitions = new HashSet<>();
			for (Vertex predecessor : vertex
					.getVertices(Direction.IN, CFLOW_LABEL))
			{
				definitions.addAll(getOut(predecessor));
			}
			reachingDefinitions.put(vertex, definitions);
		}
		return reachingDefinitions;
	}

	private LinkedList<Vertex> getAllNodes(Vertex entry)
	{
		LinkedList<Vertex> worklist = new LinkedList<>();
		GremlinPipeline<Vertex, Vertex> pipe = new
				GremlinPipeline<>();
		pipe.start(entry).as("loop")
				.out(CFLOW_LABEL).dedup().simplePath()
				.loop("loop", argument -> true, argument -> true);
		for (Vertex vertex : pipe)
		{
			worklist.add(vertex);
		}
		return worklist;
	}

	private Set<Definition> getIn(Vertex vertex)
	{
		Set<Definition> in = new HashSet<>();
		for (Vertex predecessor : vertex.getVertices(Direction.IN,
				CFLOW_LABEL))
		{
			in.addAll(getOut(predecessor));
		}
		return in;
	}

	private Set<Definition> getOut(Vertex predecessor)
	{
		Set<Definition> out = this.out.get(predecessor);
		if (null == out)
		{
			out = getGenSet(predecessor);
//			this.out.put(predecessor, out);
		}
		return out;
	}

	private Set<Definition> calculateOut(Vertex vertex, Set<Definition> in)
	{
		Set<Definition> out = new HashSet<>();
		Set<Definition> kill = getKillSet(vertex);
		Set<Definition> gen = getGenSet(vertex);
		out.addAll(in);
		out.removeAll(kill);
		out.addAll(gen);
		return out;
	}

	private Set<Definition> getGenSet(Vertex vertex)
	{
		return this.gen.apply(vertex);
	}

	private Set<Definition> getKillSet(Vertex vertex)
	{
		return this.kill.apply(vertex);
	}

}
