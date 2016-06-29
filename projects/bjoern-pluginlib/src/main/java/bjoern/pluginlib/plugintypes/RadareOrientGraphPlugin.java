package bjoern.pluginlib.plugintypes;

import bjoern.pluginlib.connectors.BjoernProjectConnector;
import octopus.lib.connectors.OrientDBConnector;
import octopus.server.components.pluginInterface.Plugin;
import org.json.JSONObject;

public abstract class RadareOrientGraphPlugin implements Plugin
{

	OrientDBConnector orientConnector = new OrientDBConnector();
	BjoernProjectConnector bjoernProjectConnector = new BjoernProjectConnector();

	private String databaseName;
	private String projectName;

	@Override
	public void configure(JSONObject settings)
	{
		projectName = settings.getString("projectName");
	}

	@Override
	public void beforeExecution() throws Exception
	{
		bjoernProjectConnector.connect(projectName);
		databaseName = bjoernProjectConnector.getWrapper().getDatabaseName();
		orientConnector.connect(databaseName);
	}

	@Override
	public void afterExecution() throws Exception
	{
		bjoernProjectConnector.disconnect();
		orientConnector.disconnect();
	}


}
