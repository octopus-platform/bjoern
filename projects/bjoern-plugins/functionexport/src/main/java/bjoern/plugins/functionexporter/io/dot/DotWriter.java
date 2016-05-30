package bjoern.plugins.functionexporter.io.dot;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DotWriter
{

	private Graph graph;
	private Writer writer;

	/**
	 * @param graph the Graph to pull the data from
	 */
	public DotWriter(final Graph graph)
	{
		this.graph = graph;
	}

	public void outputGraph(
			final OutputStream dotOutputStream) throws IOException
	{

		writer = new BufferedWriter(new OutputStreamWriter(dotOutputStream));

		writer.write(DotTokens.DIGRAPH);
		writer.write(" {");
		writer.write(DotTokens.NEWLINE);

		for (Vertex vertex : graph.getVertices())
		{
			writeVertex(vertex);
		}

		for (Edge edge : graph.getEdges())
		{
			writeEdge(edge);
		}

		writer.write("}");
		writer.write(DotTokens.NEWLINE);
		writer.flush();
	}

	private void writeEdge(Edge edge) throws IOException
	{
		Vertex tail = edge.getVertex(Direction.OUT);
		Vertex head = edge.getVertex(Direction.IN);
		writer.write(id(tail));
		writer.write(DotTokens.EDGE_OP);
		writer.write(id(head));
		writer.write(" [ ");
		writer.write("label=\"" + edge.getLabel() + "\" ");
		for (String key : edge.getPropertyKeys())
		{
			String value = edge.getProperty(key).toString();
			value = escape(value);
			writer.write(key + "=\"" + value + "\" ");
		}
		writer.write("];");
		writer.write(DotTokens.NEWLINE);
	}

	private void writeVertex(Vertex vertex) throws IOException
	{
		writer.write(id(vertex));
		writer.write(" [ ");
		for (String key : vertex.getPropertyKeys())
		{
			String value = vertex.getProperty(key).toString();
			value = escape(value);
			writer.write(key + "=\"" + value + "\" ");

		}
		writer.write("];");
		writer.write(DotTokens.NEWLINE);
	}

	private String id(Vertex vertex)
	{
		return vertex.getId().toString().split(":")[1];
	}

	private String escape(String string)
	{
		return string.replace("\"", "\\\"");
	}

	public static void outputGraph(final Graph graph,
								   final OutputStream dotOutputStream) throws IOException
	{

		DotWriter writer = new DotWriter(graph);
		writer.outputGraph(dotOutputStream);

	}
}
