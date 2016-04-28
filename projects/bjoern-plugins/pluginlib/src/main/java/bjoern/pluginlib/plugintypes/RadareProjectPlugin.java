package bjoern.pluginlib.plugintypes;

import bjoern.pluginlib.BjoernProject;
import bjoern.pluginlib.connectors.OrientDBConnector;
import bjoern.r2interface.Radare;

public class RadareProjectPlugin extends BjoernProjectPlugin {

	Radare radare = new Radare();
	BjoernProject project;
	OrientDBConnector orientConnector = new OrientDBConnector();

	@Override
	public void beforeExecution() throws Exception
	{
		project = bjoernProjectConnector.getProject();
		String r2ProjectFilename = project.getR2ProjectFilename();
		radare.loadProject(r2ProjectFilename);

		String databaseName = project.getDatabaseName();
		orientConnector.connect(databaseName);
	}

	@Override
	public void afterExecution() throws Exception
	{
		orientConnector.disconnect();
		radare.shutdown();
	}

}
