package bjoern.pluginlib.plugintypes;

import org.json.JSONObject;

import bjoern.pluginlib.connectors.BjoernProjectConnector;
import bjoern.pluginlib.connectors.OrientDBConnector;
import octopus.server.components.pluginInterface.Plugin;

public abstract class RadareOrientGraphPlugin extends Plugin {

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
		databaseName = bjoernProjectConnector.getProject().getDatabaseName();
		orientConnector.connect(databaseName);
	}

	@Override
	public void afterExecution() throws Exception
	{
		bjoernProjectConnector.disconnect();
		orientConnector.disconnect();
	}


}
