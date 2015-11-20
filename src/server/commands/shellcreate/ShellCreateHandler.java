package server.commands.shellcreate;

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

	public ShellCreateHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		OLogManager.instance().warn(this, "shellcreate");

		startShellThread();

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				OHttpUtils.CONTENT_TEXT_PLAIN,
				String.format("shell opened at: %d", lastPortNumber - 1));
		return false;
	}

	private void startShellThread()
	{
		ShellRunnable runnable = new ShellRunnable();
		runnable.setPort(lastPortNumber++);
		thread = new Thread(runnable);
		thread.start();
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|shellcreate/*" };
	}

}
