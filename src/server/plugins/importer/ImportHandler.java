package server.plugins.importer;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ImportHandler extends OServerCommandAbstract
{

	private Thread importThread;

	public ImportHandler(final OServerCommandConfiguration iConfiguration)
	{

	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		OLogManager.instance().warn(this, "Importer called");

		String codedir = getCodedirFromUrl(iRequest);
		startImporterThread(codedir);
		OLogManager.instance().warn(this, "Import Thread started");

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				OHttpUtils.CONTENT_TEXT_PLAIN, "");

		OLogManager.instance().warn(this, "Response sent.");

		return false;
	}

	private String getCodedirFromUrl(OHttpRequest iRequest)
	{
		String[] urlParts = checkSyntax(iRequest.url, 1,
				"Syntax error: importcode/<codedir>");
		return urlParts[1];
	}

	private void startImporterThread(String codedir)
	{
		importThread = new Thread(new ImportRunnable(codedir));
		importThread.start();
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|importcode/*" };
	}

}
