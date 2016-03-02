package server.base.commands.startPlugin;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

import server.base.components.pluginInterface.PluginRunnable;

public class StartPluginHandler extends OServerCommandAbstract {

	String jarName;

	public StartPluginHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		OLogManager.instance().warn(this, "startplugin");

		jarName = getJarNameFromRequest(iRequest);

		startPluginThread();

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, String.format(
				"plugin started: %s.\n", jarName), null);
		return false;

	}

	private String getJarNameFromRequest(OHttpRequest iRequest)
	{
		String[] urlParts = checkSyntax(iRequest.url, 1,
				"Syntax error: startplugin/<jarName>");
		return urlParts[1];
	}

	private void startPluginThread()
	{
		PluginRunnable runnable = new PluginRunnable();
		runnable.setJarName(jarName);
		Thread thread = new Thread(runnable);
		thread.start();
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|loadplugin/*" };
	}
}
