package server.commands.shellcreate;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class ShellCreateHandler extends OServerCommandAbstract
{

	public ShellCreateHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
			throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getNames()
	{
		return new String[] { "GET|shellcreate/*" };
	}

}
