package server.commands.dumpcfg;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequestException;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import server.Constants;
import server.components.graphs.CFGCreator;

public class OServerCommandGetDumpCFG extends OServerCommandAbstract
{
	private static final String[] NAMES = { "GET|dumpcfg/*" };
	private static final Logger logger = LoggerFactory
			.getLogger(OServerCommandGetDumpCFG.class);

	private Path baseDir = Paths.get(Constants.FALLBACK_DATA_DIR);
	private OpenOption[] openOptions = { StandardOpenOption.CREATE_NEW };
	private String databaseName;
	private int nThreads = 1;
	OrientGraphFactory factory;
	private OrientGraphNoTx g;

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
			case "threads":
				try
				{
					nThreads = Integer.parseInt(par.value);
				} finally
				{
					if (nThreads < 1)
					{
						nThreads = 1;
					}
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
		factory = new OrientGraphFactory(
				Constants.PLOCAL_REL_PATH_TO_DBS + databaseName);
		factory.setupPool(1, 10);

		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		g = factory.getNoTx();

		for (Vertex functionNode : getFunctionNodes())
		{
			executor.execute(new Runnable()
			{

				@Override
				public void run()
				{

					String id = functionNode.getId().toString();
					Long functionId = Long.parseLong(id.split(":")[1]);
					OrientGraphNoTx g = factory.getNoTx();
					try
					{
						Path path = getOutputDestination(functionId);
						Files.createDirectories(path.getParent());
						CFGCreator cfgCreator = new CFGCreator(g);
						Graph cfg = cfgCreator.createCFG(functionId);
						dumpGraph(cfg, path);
						logger.info("Writing control flow graph of function "
								+ functionId + " to file " + path.toString()
								+ ".");
					} catch (FileAlreadyExistsException e)
					{
						logger.warn("Skipping function " + functionId
								+ ". File exists: " + e.getMessage());
					} catch (IOException e)
					{
						logger.error("Skipping function " + functionId
								+ ". IO Exception: " + e.getMessage());
					} catch (ODatabaseException e)
					{
						logger.error(e.getMessage());
					} finally
					{
						g.shutdown();
					}
				}
			});
		}

		factory.close();

		executor.shutdown();
		while (!executor.isTerminated())
		{
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				baseDir.toString() + "\n", null);
		return false;
	}

	protected void dumpGraph(Graph graph, Path path) throws IOException
	{
		OutputStream out = Files.newOutputStream(path, openOptions);
		GraphMLWriter.outputGraph(graph, out);
		out.close();
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
