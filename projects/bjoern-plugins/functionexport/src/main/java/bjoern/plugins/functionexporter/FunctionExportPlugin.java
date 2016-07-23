package bjoern.plugins.functionexporter;

import bjoern.structures.BjoernNodeTypes;
import bjoern.pluginlib.LookupOperations;
import bjoern.plugins.functionexporter.io.dot.DotWriter;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

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
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FunctionExportPlugin extends OrientGraphConnectionPlugin
{

	private static final String DEFAULT_FORMAT = "graphml";
	private static final int DEFAULT_NUMBER_OF_THREADS = 4;

	private static final String[] DEFAULT_NODES = {BjoernNodeTypes.FUNCTION, BjoernNodeTypes.BASIC_BLOCK, BjoernNodeTypes.INSTRUCTION};
	private static final String[] DEFAULT_EDGES = {EdgeTypes.IS_FUNCTION_OF, EdgeTypes.IS_BB_OF};

	private String format;
	private ExecutorService executor;
	private int nThreads;
	private Path outputDirectory;
	private String[] nodes;
	private String[] edges;

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

		// read the outputDirectory folder
		configureOutputDirectoru(settings);

		// read the node and edge types to include in the exported graph
		configureNodes(settings);
		configureEdges(settings);
	}

	private void configureFormat(JSONObject settings)
	{
		if (settings.has("format"))
		{
			format = settings.getString("format");
		} else
		{
			format = DEFAULT_FORMAT;
		}
	}

	private void configureNumberOfThreads(JSONObject settings)
	{
		if (settings.has("threads"))
		{
			nThreads = settings.getInt("threads");
		} else
		{
			nThreads = DEFAULT_NUMBER_OF_THREADS;
		}
	}

	private void configureOutputDirectoru(JSONObject settings)
	{
		outputDirectory = Paths.get(settings.getString("outdir"),
				getDatabaseName());
	}

	private void configureNodes(JSONObject settings)
	{
		if (settings.has("nodes"))
		{
			readNodeList(settings.getJSONArray("nodes"));
		} else
		{
			nodes = DEFAULT_NODES;
		}
	}

	private void configureEdges(JSONObject settings)
	{
		if (settings.has("edges"))
		{
			readEdgeList(settings.getJSONArray("edges"));
		} else
		{
			nodes = DEFAULT_EDGES;
		}
	}

	private void readNodeList(JSONArray nodeList)
	{
		nodes = new String[nodeList.length()];
		for (int i = 0; i < nodeList.length(); i++)
		{
			nodes[i] = nodeList.getString(i);
		}
	}

	private void readEdgeList(JSONArray edgeList)
	{
		edges = new String[edgeList.length()];
		for (int i = 0; i < edgeList.length(); i++)
		{
			edges[i] = edgeList.getString(i);
		}
	}

	@Override
	public void beforeExecution() throws Exception
	{
		// calling super here is important since it opens the database
		// connection for you.
		super.beforeExecution();
		executor = Executors.newFixedThreadPool(nThreads);
		Files.createDirectories(outputDirectory);
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
				Path out = Paths.get(outputDirectory.toString(), "func" +
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
						v -> Arrays.asList(nodes)
								.contains(v.getObject().getProperty(BjoernNodeProperties.TYPE).toString()));

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
						v -> Arrays.asList(nodes)
								.contains(v.getObject().getProperty(BjoernNodeProperties.TYPE).toString()))
				.outE(edges);

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
