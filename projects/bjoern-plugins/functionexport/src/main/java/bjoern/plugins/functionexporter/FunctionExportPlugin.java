package bjoern.plugins.functionexporter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.json.JSONArray;
import org.json.JSONObject;

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

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.plugintypes.OrientGraphConnectionPlugin;
import bjoern.plugins.functionexporter.io.dot.DotWriter;

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
		configureFormat(settings);

		// read the number of threads to use
		configureNumberOfThreads(settings);

		// read the destination folder
		configureOutputDirectoru(settings);

		// read the node and edge types to include in the exported graph
		configureNodes(settings);
		configureEdges(settings);
	}

	private void configureEdges(JSONObject settings)
	{
		readEdgeList(settings.getJSONArray("edges"));
	}

	private void configureNodes(JSONObject settings)
	{
		readNodeList(settings.getJSONArray("nodes"));
	}

	private void configureOutputDirectoru(JSONObject settings)
	{
		destination = Paths.get(settings.getString("destination"),
				getDatabaseName());
	}

	private void configureNumberOfThreads(JSONObject settings)
	{
		nThreads = settings.getInt("threads");
	}

	private void configureFormat(JSONObject settings)
	{
		format = settings.getString("format");
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
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();

		Iterable<Vertex> functions = LookupOperations.getAllFunctions(graph);

		for (Vertex function : functions)
		{
			exportFunction(function);
		}

		graph.shutdown();
	}

	private void exportFunction(Vertex vertex) throws IOException
	{

		executor.execute(() -> {
			Graph graph = orientConnector.getNoTxGraphInstance();
			try
			{
				Vertex functionRoot = graph.getVertex(vertex);
				Graph subgraph = new TinkerGraph();
				copyFunctionNodes(subgraph, functionRoot);
				copyFunctionEdges(subgraph, functionRoot);
				subgraph.shutdown();
				Path out = Paths.get(destination.toString(), "func" +
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
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
		pipe.start(functionRoot).as("loop")
				.out(EdgeTypes.IS_FUNCTION_OF, EdgeTypes.IS_BB_OF, EdgeTypes.READ, EdgeTypes.WRITE)
				.loop("loop", v -> true,
						v -> nodes.contains(v.getObject().getProperty(BjoernNodeProperties.TYPE).toString()));

		for (Vertex v : pipe)
		{
			copyVertex(graph, v);
		}

	}

	private void copyFunctionEdges(Graph graph, Vertex functionRoot)
	{
		GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<>();
		pipe.start(functionRoot).as("loop")
				.out(EdgeTypes.IS_FUNCTION_OF, EdgeTypes.IS_BB_OF, EdgeTypes.READ, EdgeTypes.WRITE)
				.loop("loop", v -> true,
						v -> nodes.contains(v.getObject().getProperty(BjoernNodeProperties.TYPE).toString()))
				.outE(edges.toArray(new String[edges.size()]));

		for (Edge e : pipe)
		{
			copyEdge(graph, e);
		}
	}

	private static void copyVertex(Graph graph, Vertex vertex)
	{
		Object id = vertex.getId();
		if (graph.getVertex(id) != null)
		{
			return;
		}
		Vertex v = GraphHelper.addVertex(graph, id);
		if (v != null)
		{
			ElementHelper.copyProperties(vertex, v);
		}
	}

	private static void copyEdge(Graph graph, Edge edge)
	{
		Object id = edge.getId();
		if (graph.getEdge(id) != null)
		{
			return;
		}
		Vertex src = graph.getVertex(edge.getVertex(Direction.OUT).getId());
		Vertex dst = graph.getVertex(edge.getVertex(Direction.IN).getId());
		if (src != null && dst != null)
		{
			Edge e = GraphHelper.addEdge(graph, id, src, dst, edge.getLabel());
			if (e != null)
			{
				ElementHelper.copyProperties(edge, e);
			}
		}
	}
}
