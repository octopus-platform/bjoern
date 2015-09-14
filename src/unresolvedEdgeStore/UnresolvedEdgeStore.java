package unresolvedEdgeStore;

import java.util.LinkedList;
import java.util.List;

import structures.edges.DirectedEdge;

/**
 * We keep all nodes for which source or destination node cannot be resolved in
 * this store. Output modules can access the store to store these nodes for
 * later processing.
 */

public class UnresolvedEdgeStore
{
	static List<DirectedEdge> edges = new LinkedList<DirectedEdge>();

	public static void clearCache()
	{
		edges.clear();
	}

	public static void add(DirectedEdge edge)
	{
		edges.add(edge);
	}

	public static List<DirectedEdge> getEdges()
	{
		return edges;
	}

}
