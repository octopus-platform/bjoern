package bjoern.plugins.datadependence;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.*;

public class ReachingDefinitionAnalyser
{
	private static final String[] CFLOW_LABEL = {"NEXT_INSTR",
			"NEXT_INSTR_TRANSITIVE"};
	private static final String[] DEF_LABEL = {"WRITE"};
	private Map<Vertex, Set<Edge>> out;

	/**
	 * Calculate the reaching definitions for the given flow graph {@code
	 * graph} with entry node {@code entry}. Each node of the flow graph must
	 * be connected to a set of nodes representing the object defined by this
	 * node as well as nodes representing the objects used by this node.
	 *
	 * @param graph the flow graph
	 * @param entry the entry node
	 * @return a mapping from nodes to all reaching definitions
	 */
	public Map<Vertex, Set<Edge>> analyse(Graph graph, Vertex entry)
	{
		this.out = new HashMap<>();
		Queue<Vertex> worklist = getAllNodes(entry);
		while (!worklist.isEmpty())
		{
			Vertex vertex = worklist.remove();
			Set<Edge> in = getIn(vertex);
			Set<Edge> outNew = calculateOut(vertex, in);
			Set<Edge> outOld = getOut(vertex);
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

	private Map<Vertex, Set<Edge>> collectReachingDefinitions()
	{
		Map<Vertex, Set<Edge>> reachingDefinitions = new HashMap<>();
		for (Vertex vertex : out.keySet())
		{
			Set<Edge> definitions = new HashSet<>();
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

	private Set<Edge> getIn(Vertex vertex)
	{
		Set<Edge> in = new HashSet<>();
		for (Vertex predecessor : vertex.getVertices(Direction.IN,
				CFLOW_LABEL))
		{
			in.addAll(getOut(predecessor));
		}
		return in;
	}

	private Set<Edge> getOut(Vertex predecessor)
	{
		Set<Edge> out = this.out.get(predecessor);
		if (null == out)
		{
			out = getGenSet(predecessor);
//			this.out.put(predecessor, out);
		}
		return out;
	}

	private Set<Edge> calculateOut(Vertex vertex, Set<Edge> in)
	{
		Set<Edge> out = new HashSet<>();
		Set<Edge> kill = getKillSet(vertex);
		Set<Edge> gen = getGenSet(vertex);
		out.addAll(in);
		out.removeAll(kill);
		out.addAll(gen);
		return out;
	}

	private Set<Edge> getGenSet(Vertex vertex)
	{
		Set<Edge> set = new HashSet<>();
		for (Edge edge : vertex.getEdges(Direction.OUT, DEF_LABEL))
		{
			set.add(edge);
		}
		return set;
	}

	private Set<Edge> getKillSet(Vertex vertex)
	{
		Set<Edge> set = new HashSet<>();
		for (Vertex x : vertex.getVertices(Direction.OUT, DEF_LABEL))
		{
			for (Edge edge : x.getEdges(Direction.IN, DEF_LABEL))
			{
				set.add(edge);
			}
		}
		return set;
	}

}
