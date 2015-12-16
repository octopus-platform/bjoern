package server.components.graphs;

import java.util.LinkedList;
import java.util.List;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import server.Constants;

public class CFGCreator extends GraphCreator
{

	protected OrientGraphNoTx g;

	public CFGCreator(OrientGraphNoTx graph)
	{
		this.g = graph;
	}

	public Graph createCFG(Long functionId)
	{
		Iterable<Vertex> vertices = getVerticesOfFunction(functionId);

		Iterable<Edge> edges = getInducedEdges(vertices,
				vertex -> vertex.getEdges(Direction.OUT),
				edge -> (edge.getLabel().equals("CFLOW_ALWAYS")
						|| edge.getLabel().equals("CFLOW_TRUE")
						|| edge.getLabel().equals("CFLOW_FALSE")
						|| edge.getLabel().equals("IS_BB_OF"))
						&& edge.getVertex(Direction.IN)
								.getProperty("functionId")
								.equals(functionId.toString()));

		return createGraph(vertices, edges);
	}

	protected Iterable<Vertex> getVerticesOfFunction(Long functionId)
	{
		String fmt = "SELECT * FROM V WHERE %s LUCENE \"%s\"";
		String luceneQuery = "functionId:" + functionId;
		String queryStr = String.format(fmt, Constants.INDEX_NAME, luceneQuery);

		OCommandSQL query = new OCommandSQL(queryStr);
		List<Vertex> vertices = new LinkedList<Vertex>();
		Iterable<Vertex> result = g.command(query).execute();
		for (Vertex obj : result)
		{
			vertices.add(obj);
		}
		return vertices;
	}

}
