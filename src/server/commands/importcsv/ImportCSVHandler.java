package server.commands.importcsv;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ImportCSVHandler extends OServerCommandAbstract
{

	private Thread importThread;

	public ImportCSVHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		OLogManager.instance().warn(this, "Importer called");

		GraphFiles graphFiles = getCodedirFromUrl(iRequest);
		startImporterThread(graphFiles);
		OLogManager.instance().warn(this, "Import Thread started");

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				OHttpUtils.CONTENT_TEXT_PLAIN, "");

		OLogManager.instance().warn(this, "Response sent.");

		return false;
	}

	private GraphFiles getCodedirFromUrl(OHttpRequest iRequest)
	{
		String[] urlParts = checkSyntax(iRequest.url, 2,
				"Syntax error: importcsv/<nodeFilename>/<edgeFilename>");
		return new GraphFiles(urlParts[1], urlParts[2]);
	}

	private void startImporterThread(GraphFiles graphFiles)
	{
		importThread = new Thread(new ImportCSVRunnable(graphFiles));
		importThread.start();
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|importcsv/*" };
	}

}
