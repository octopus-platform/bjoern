package server.bjoern.commands.dumpcfg;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequestException;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import server.Constants;
import server.bjoern.components.cfgdump.CFGDumpRunnable;
import server.bjoern.components.cfgdump.CFGDumpService;

public class OServerCommandGetDumpCFG extends OServerCommandAbstract
{
	private static final Logger logger = LoggerFactory
			.getLogger(OServerCommandGetDumpCFG.class);
	private static final String[] NAMES = { "GET|dumpcfg/*" };
	private static final int N_THREADS = 1;

	private Path baseDir = Paths.get(Constants.FALLBACK_DATA_DIR);
	private int nThreads = N_THREADS;
	private String format = CFGDumpRunnable.GRAPHML_FORMAT;

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
			case "threads":
				readThreadsParameter(par);
				break;
			case "format":
				readFormatParameter(par);
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

	private void readFormatParameter(OServerEntryConfiguration par)
	{
		switch (par.value)
		{
		case "graphml":
		case "GRAPHML":
			format = CFGDumpRunnable.GRAPHML_FORMAT;
			break;
		case "gml":
		case "GML":
			format = CFGDumpRunnable.GML_FORMAT;
			break;
		default:
			logger.error(
					"Invalid parameter value: Unknown format " + par.value);
		}
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		String[] urlParts = checkSyntax(iRequest.url);
		String databaseName = urlParts[1];

		OrientGraphNoTx g = new OrientGraphNoTx(
				Constants.PLOCAL_REL_PATH_TO_DBS + databaseName);

		CFGDumpService service = new CFGDumpService(databaseName, baseDir,
				nThreads, format);
		for (Vertex functionNode : getFunctionNodes(g))
		{
			service.dumpCFG(functionNode);
		}
		g.shutdown();

		// Wait until all work is done.
		service.shutDown();
		service.awaitTermination();
		iResponse.send(
				OHttpUtils.STATUS_OK_CODE, "OK", null, "Data written to "
						+ baseDir.toString() + "/cfg/" + databaseName + "/.\n",
				null);

		return false;
	}

	protected static Iterable<Vertex> getFunctionNodes(OrientBaseGraph g)
	{
		return g.getVertices("V", Constants.INDEX_KEYS,
				new String[] { "nodeType:Func" });
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
