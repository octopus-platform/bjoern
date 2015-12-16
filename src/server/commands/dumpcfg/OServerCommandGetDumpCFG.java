package server.commands.dumpcfg;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequestException;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import server.Constants;

public class OServerCommandGetDumpCFG extends OServerCommandAbstract
{
	private static final String[] NAMES = { "GET|dumpcfg/*" };
	private static final Logger logger = LoggerFactory
			.getLogger(OServerCommandGetDumpCFG.class);

	private OrientGraphNoTx g;
	private Path baseDir = Paths.get(Constants.FALLBACK_DATA_DIR);
	private OpenOption[] openOptions = { StandardOpenOption.CREATE_NEW };
	private String databaseName;

	public OServerCommandGetDumpCFG(
			final OServerCommandConfiguration iConfiguration)
	{
		for (OServerEntryConfiguration par : iConfiguration.parameters)
		{
			switch (par.name)
			{
			case "dest":
				baseDir = Paths.get(par.value).toAbsolutePath().normalize();
				break;
			case "force":
				if (Boolean.parseBoolean(par.value))
				{
					openOptions = new OpenOption[] { StandardOpenOption.CREATE,
							StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.WRITE };
				}
				break;
			}
		}
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		String[] urlParts = checkSyntax(iRequest.url);
		databaseName = urlParts[1];

		g = new OrientGraphFactory(
				Constants.PLOCAL_REL_PATH_TO_DBS + databaseName).getNoTx();

		for (Vertex functionNode : getFunctionNodes())
		{
			String id = functionNode.getId().toString();
			Long functionId = Long.parseLong(id.split(":")[1]);
			dumpCFG(functionId);
		}

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, "OK\n", null);
		return false;
	}

	private void dumpCFG(Long functionId) throws IOException
	{
		try
		{
			Path path = getOutputDestination(functionId);
			Files.createDirectories(path.getParent());
			Graph cfg = fetchCFG(functionId);
			dumpGraph(cfg, path);
			logger.info("Writing control flow graph of function " + functionId
					+ " to file " + path.toString());
		} catch (IOException e)
		{
			logger.warn("Skipping function " + functionId + ". File exists.");
		}
	}

	protected Graph fetchCFG(Long functionId)
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

	protected Graph createGraph(Iterable<Vertex> vertices, Iterable<Edge> edges)
	{
		Graph sg = new TinkerGraph();
		for (Vertex vertex : vertices)
		{
			Vertex v = sg.addVertex(vertex.getId());
			for (String property : vertex.getPropertyKeys())
			{
				v.setProperty(property, vertex.getProperty(property));
			}
		}
		for (Edge edge : edges)
		{
			Vertex outVertex = sg
					.getVertex(edge.getVertex(Direction.OUT).getId());
			Vertex inVertex = sg
					.getVertex(edge.getVertex(Direction.IN).getId());
			Edge e = sg.addEdge(edge.getId(), outVertex, inVertex,
					edge.getLabel());
			for (String property : edge.getPropertyKeys())
			{
				e.setProperty(property, edge.getProperty(property));
			}
		}

		return sg;

	}

	protected void dumpGraph(Graph graph, Path path) throws IOException
	{
		OutputStream out = Files.newOutputStream(path, openOptions);
		GraphMLWriter.outputGraph(graph, out);
		out.close();
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

	protected Iterable<Edge> getInducedEdges(Iterable<Vertex> vertices,
			Function<Vertex, Iterable<Edge>> f, Predicate<Edge> p)
	{
		List<Edge> edges = new LinkedList<Edge>();
		for (Vertex vertex : vertices)
		{
			for (Edge edge : f.apply(vertex))
			{
				if (p.test(edge))
				{
					edges.add(edge);
				}
			}
		}
		return edges;
	}

	protected Iterable<Vertex> getFunctionNodes()
	{
		String fmt = "SELECT * FROM V WHERE %s LUCENE \"nodeType:Func\"";
		String queryStr = String.format(fmt, Constants.INDEX_NAME);

		OCommandSQL query = new OCommandSQL(queryStr);
		Iterable<Vertex> result = g.command(query).execute();
		return result;
	}

	protected String[] checkSyntax(String url)
	{
		String syntax = "Syntax error: dumpcfg/dbname";
		String[] urlParts = checkSyntax(url, 2, syntax);
		if (urlParts[1].equals(""))
		{
			throw new OHttpRequestException(syntax);
		}
		return urlParts;
	}

	private Path getOutputDestination(long functionId)
	{
		String filename = databaseName + "-cfg" + functionId + ".graphml";
		Path dest = Paths.get(baseDir.toString(), filename);
		return dest.toAbsolutePath().normalize();
	}

	@Override
	public String[] getNames()
	{
		return NAMES;
	}

}
