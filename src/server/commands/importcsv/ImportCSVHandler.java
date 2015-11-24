package server.commands.importcsv;

import server.DebugPrinter;
import server.components.orientdbImporter.ImportCSVRunnable;
import server.components.orientdbImporter.ImportJob;

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
		DebugPrinter.print("Importer called", this);

		ImportJob importJob = getImportJobFromRequest(iRequest);

		startImporterThread(importJob);
		OLogManager.instance().warn(this, "Import Thread started");

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				OHttpUtils.CONTENT_TEXT_PLAIN, "");

		OLogManager.instance().warn(this, "Response sent.");

		return false;
	}

	private ImportJob getImportJobFromRequest(OHttpRequest iRequest)
	{
		String[] urlParts = checkSyntax(
				iRequest.url,
				4,
				"Syntax error: importcsv/<nodeFilename>/<edgeFilename>/<dbName>/[unedgeFilename]");

		String unedgeFilename = "";
		if (urlParts.length >= 5)
			unedgeFilename = urlParts[4];

		return new ImportJob(urlParts[1], urlParts[2], urlParts[3],
				unedgeFilename);
	}

	private void startImporterThread(ImportJob graphFiles)
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
