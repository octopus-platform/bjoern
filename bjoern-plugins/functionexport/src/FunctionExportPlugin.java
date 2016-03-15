import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.blueprints.util.GraphHelper;
import com.tinkerpop.blueprints.util.io.gml.GMLWriter;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import com.tinkerpop.blueprints.util.wrappers.batch.cache.ObjectIDVertexCache;
import io.dot.DotWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import server.base.components.pluginInterface.OrientGraphConnectionPlugin;
import server.bjoern.BjoernConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FunctionExportPlugin extends OrientGraphConnectionPlugin
{

	private String format;
	private ExecutorService executor;
	private int nThreads;
	private Path destination;
	private ArrayList<String> nodes;
	private ArrayList<String> edges;

	@Override
	public void configure(JSONObject settings)
	{
		// calling super here is important. It reads the database name from
		// the json object.
		super.configure(settings);
		// read the output format (graphml, dot, etc.)
		format = settings.getString("format");
		// read the number of threads to use
		nThreads = settings.getInt("threads");
		// read the destination folder
		destination = Paths.get(settings.getString("destination"),
				getDatabaseName());

		// read the node and edge types to include in the exported graph
		readNodeList(settings.getJSONArray("nodes"));
		readEdgeList(settings.getJSONArray("edges"));
	}

	private void readNodeList(JSONArray nodeList)
	{
		nodes = new ArrayList<>(nodeList.length());
		for (int i = 0; i < nodeList.length(); i++)
		{
			nodes.add(nodeList.getString(i));
		}
	}

	private void readEdgeList(JSONArray edgeList)
	{
		edges = new ArrayList<>(edgeList.length());
		for (int i = 0; i < edgeList.length(); i++)
		{
			edges.add(edgeList.getString(i));
		}
	}

	@Override
	public void beforeExecution() throws Exception
	{
		// calling super here is important since it opens the database
		// connection for you.
		super.beforeExecution();
		executor = Executors.newFixedThreadPool(nThreads);
		Files.createDirectories(destination);
	}

	@Override
	public void afterExecution() throws Exception
	{
		try
		{
			executor.shutdown();
			awaitTermination();
		} finally
		{
			// calling super here is important since it closes the database
			// connection for you.
			super.afterExecution();
		}
	}

	private void awaitTermination() throws InterruptedException
	{
		while (!executor.isTerminated())
		{
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
	}

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = getNoTxGraphInstance();

		Iterable<Vertex> functions = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:Func");


		for (Vertex function : functions)
		{
			exportFunction(function);
		}

		graph.shutdown();
	}

	private void exportFunction(Vertex vertex) throws IOException
	{

		executor.execute(new Runnable()
		{
			@Override
			public void run()
			{
				Graph graph = getNoTxGraphInstance();
				try
				{
					Vertex functionRoot = graph.getVertex(vertex);
					Graph subgraph = new TinkerGraph();
					copyFunctionNodes(subgraph, functionRoot);
					copyFunctionEdges(subgraph, functionRoot);
					subgraph.shutdown();
					Path out = Paths.get(destination.toString(), "cfg" +
							functionRoot.getId().toString().split(":")[1] +
							"." + format);
					writeGraph(subgraph, out);
				} catch (IOException e)
				{
					e.printStackTrace();
				} finally
				{
					graph.shutdown();
				}
			}
		});

	}


	private void writeGraph(Graph graph, Path path) throws IOException
	{
		OutputStream out = Files.newOutputStream(path);
		switch (format)
		{
			case "graphml":
				GraphMLWriter.outputGraph(graph, out);
				break;
			case "gml":
				GMLWriter.outputGraph(graph, out);
				break;
			case "dot":
				DotWriter.outputGraph(graph, out);
				break;
			default:
				GraphMLWriter.outputGraph(graph, out);
				break;
		}
	}

	private void copyFunctionNodes(Graph graph, Vertex functionRoot)
	{
		copyVertex(graph, functionRoot);
		for (Edge isFuncOfEdge : functionRoot.getEdges(Direction.OUT,
				"IS_FUNC_OF"))
		{
			Vertex bb = isFuncOfEdge.getVertex(Direction.IN);
			copyVertex(graph, bb);
			for (Edge isBBOfEdge : bb.getEdges(Direction.OUT, "IS_BB_OF"))
			{
				Vertex instr = isBBOfEdge.getVertex(Direction.IN);
				copyVertex(graph, instr);
			}
		}
	}

	private void copyFunctionEdges(Graph graph, Vertex functionRoot)
	{

		for (Edge edge1 : functionRoot.getEdges(Direction.OUT))
		{
			copyEdge(graph, edge1);
			Vertex vertex1 = edge1.getVertex(Direction.IN);
			for (Edge edge2 : vertex1.getEdges(Direction.OUT))
			{
				copyEdge(graph, edge2);
				Vertex vertex2 = edge2.getVertex(Direction.IN);
				for (Edge edge3 : vertex2.getEdges(Direction.OUT))
				{
					copyEdge(graph, edge3);
				}
			}
		}
	}

	private void copyVertex(Graph graph, Vertex vertex)
	{
		if (!nodes.contains(vertex.getProperty("nodeType").toString()))
		{
			return;
		}
		Object id = vertex.getId();
		if (graph.getVertex(id) != null) {
			return;
		}
		Vertex v = GraphHelper.addVertex(graph, id);
		if (v != null)
		{
			ElementHelper.copyProperties(vertex, v);
		}
	}

	private void copyEdge(Graph graph, Edge edge)
	{
		String label = edge.getLabel();
		if (!edges.contains(label))
		{
			return;
		}
		Object id = edge.getId();
		if (graph.getEdge(id) != null)
		{
			return;
		}
		Vertex src = graph.getVertex(edge.getVertex(Direction.OUT).getId());
		Vertex dst = graph.getVertex(edge.getVertex(Direction.IN).getId());
		if (src != null && dst != null)
		{
			Edge e = GraphHelper.addEdge(graph, id, src, dst, label);
			if (e != null)
			{
				ElementHelper.copyProperties(edge, e);
			}
		}
	}
}
