package unresolvedEdgeStore;

import java.util.LinkedList;
import java.util.List;

public class UnresolvedEdgeStore
{
	static List<UnresolvedEdge> edges = new LinkedList<UnresolvedEdge>();

	public static void clearCache()
	{
		edges.clear();
	}

	public static void add(UnresolvedEdge edge)
	{
		edges.add(edge);
	}

	public static List<UnresolvedEdge> getEdges()
	{
		return edges;
	}

}
