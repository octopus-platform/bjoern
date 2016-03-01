package server.base.commands.listshells;

import java.util.List;

import server.base.components.gremlinShell.BjoernGremlinShell;
import server.base.components.shellmanager.ShellManager;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ListShellsHandler extends OServerCommandAbstract
{
	public ListShellsHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		List<BjoernGremlinShell> shells = ShellManager.getActiveShells();
		StringBuilder sb = new StringBuilder();
		for (BjoernGremlinShell shell : shells)
		{
			sb.append(rowForShell(shell));
		}

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, sb.toString(),
				null);

		return false;
	}

	private Object rowForShell(BjoernGremlinShell shell)
	{
		return String.format("%d\t%s\n", shell.getPort(), shell.getDbName());
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|listshells/*" };
	}

}
