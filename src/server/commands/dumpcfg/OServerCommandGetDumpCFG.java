package server.commands.dumpcfg;

import java.io.IOException;
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

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequestException;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import server.Constants;
import server.components.graphs.CFGDumpRunnable;

public class OServerCommandGetDumpCFG extends OServerCommandAbstract
{
	private static final Logger logger = LoggerFactory
			.getLogger(OServerCommandGetDumpCFG.class);
	private static final String[] NAMES = { "GET|dumpcfg/*" };
	private static final int N_THREADS = 1;

	private Path baseDir = Paths.get(Constants.FALLBACK_DATA_DIR);
	private OpenOption[] openOptions = { StandardOpenOption.CREATE_NEW };
	private int nThreads = N_THREADS;

	public OServerCommandGetDumpCFG(
			final OServerCommandConfiguration iConfiguration) throws IOException
	{
		readCommandParameter(iConfiguration);
	}

	private void readCommandParameter(
			OServerCommandConfiguration iConfiguration)
	{
		for (OServerEntryConfiguration par : iConfiguration.parameters)
		{
			switch (par.name)
			{
			case "dest":
				readDestParameter(par);
				break;
			case "force":
				readForceParameter(par);
				break;
			case "threads":
				readThreadsParameter(par);
				break;
			}
		}
	}

	private void readThreadsParameter(OServerEntryConfiguration par)
	{
		try
		{
			nThreads = Integer.parseInt(par.value);
		} catch (NumberFormatException e)
		{
			logger.error(
					"Invalid parameter value: Not an integer " + par.value);
		}
		if (nThreads < 1)
		{
			nThreads = N_THREADS;
		}
	}

	private void readDestParameter(OServerEntryConfiguration parameter)
	{
		baseDir = Paths.get(parameter.value).toAbsolutePath().normalize();
	}

	private void readForceParameter(OServerEntryConfiguration parameter)
	{
		if (Boolean.parseBoolean(parameter.value))
		{
			openOptions = new OpenOption[] { StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE };
		}
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		Files.createDirectories(baseDir);
		String[] urlParts = checkSyntax(iRequest.url);
		String databaseName = urlParts[1];
		OrientGraphFactory factory = new OrientGraphFactory(
				Constants.PLOCAL_REL_PATH_TO_DBS + databaseName).setupPool(1,
						10);
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		OrientGraphNoTx g = factory.getNoTx();

		for (Vertex functionNode : getFunctionNodes(g))
		{
			CFGDumpRunnable runnable = new CFGDumpRunnable(factory,
					functionNode, baseDir, openOptions);

			executor.execute(runnable);
		}

		g.shutdown();
		factory.close();
		executor.shutdown();

		// Wait until all work is done.
		while (!executor.isTerminated())
		{
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				baseDir.toString() + "\n", null);
		return false;
	}

	protected static Iterable<Vertex> getFunctionNodes(OrientBaseGraph g)
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

	@Override
	public String[] getNames()
	{
		return NAMES;
	}

}
