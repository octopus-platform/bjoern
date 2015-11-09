package tools.server;

import tools.orientdbImporter.BatchImporter;
import tools.radareExporter.RadareExporter;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ImportHandler extends OServerCommandAbstract
{

	public ImportHandler(final OServerCommandConfiguration iConfiguration)
	{

	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		String[] urlParts = checkSyntax(iRequest.url, 1,
				"Syntax error: importcode/<codedir>");

		String codeDir = urlParts[1];
		codeDir = codeDir.replace("|", "/");

		RadareExporter.main(new String[] { codeDir });
		BatchImporter.main(new String[] { "nodes.csv", "edges.csv" });

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				OHttpUtils.CONTENT_TEXT_PLAIN, "");

		return false;
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|importcode/*" };
	}

}
