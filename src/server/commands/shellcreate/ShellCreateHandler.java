package server.commands.shellcreate;

import server.commands.Constants;
import server.components.gremlinShell.ShellRunnable;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ShellCreateHandler extends OServerCommandAbstract
{

	private Thread thread;
	private static int lastPortNumber = 6000;
	private String dbName;

	public ShellCreateHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		OLogManager.instance().warn(this, "shellcreate");

		dbName = getDbNameFromRequest(iRequest);

		startShellThread();

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				String.format("shell opened at: %d", lastPortNumber - 1), null);
		return false;
	}

	private String getDbNameFromRequest(OHttpRequest iRequest)
	{
		String[] urlParts = checkSyntax(iRequest.url, 0,
				"Syntax error: shellcreate/[dbName]");

		if (urlParts.length >= 2)
			return urlParts[1];
		return Constants.DEFAULT_DB_NAME;
	}

	private void startShellThread()
	{
		ShellRunnable runnable = new ShellRunnable();
		runnable.setPort(lastPortNumber++);
		runnable.setDbName(dbName);
		thread = new Thread(runnable);
		thread.start();
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|shellcreate/*" };
	}

}
