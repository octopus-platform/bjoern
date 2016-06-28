package octopus.server.commands.shellmanager;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import octopus.server.Constants;
import octopus.server.components.gremlinShell.OctopusGremlinShell;
import octopus.server.components.gremlinShell.ShellRunnable;
import octopus.server.components.shellmanager.ShellManager;

/**
 * Created by alwin on 6/27/16.
 */
public class ManageShellsHandler extends OServerCommandAbstract
{
	private String dbName;

	public ManageShellsHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{

		String[] urlParts = checkSyntax(iRequest.url, 2, "Syntax error: manageshells/<cmd>/[projectName]");

		String command = urlParts[1];

		switch (command)
		{
			case "list":
				return executeList(iRequest, iResponse);
			case "create":
				return executeCreate(iRequest, iResponse);
			default:
				iResponse.send(OHttpUtils.STATUS_NOTFOUND_CODE, "Not found", null, "", null);
				return false;
		}
	}

	private boolean executeList(OHttpRequest iRequest, OHttpResponse iResponse) throws Exception
	{
		checkSyntax(iRequest.url, 2, "Syntax error: manageshells/list");
		StringBuilder sb = new StringBuilder();
		for (OctopusGremlinShell shell : ShellManager.getActiveShells())
		{
			sb.append(rowForShell(shell));
			sb.append('\n');

		}
		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, sb.toString(), null);
		return false;
	}

	private boolean executeCreate(OHttpRequest iRequest, OHttpResponse iResponse) throws Exception
	{
		String[] urlParts = checkSyntax(iRequest.url, 3, "Syntax error: manageshells/create/projectName");
		startShellThread(urlParts[2]);
		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, "", null);
		return false;
	}

	private void startShellThread(String databaseName)
	{
		ShellRunnable runnable = new ShellRunnable();
		runnable.setDbName(databaseName);
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private Object rowForShell(OctopusGremlinShell shell)
	{
		return String.format("%d\t%s", shell.getPort(), shell.getDbName());
	}

	@Override
	public String[] getNames()
	{
		return new String[]{"GET|manageshells/*"};
	}

}
