package bjoern.pluginlib.plugintypes;

import java.io.IOException;

import bjoern.pluginlib.BjoernProject;
import bjoern.pluginlib.connectors.OrientDBConnector;
import bjoern.r2interface.Radare;

public class RadareProjectPlugin extends BjoernProjectPlugin {

	protected Radare radare = new Radare();
	protected BjoernProject project;
	protected OrientDBConnector orientConnector = new OrientDBConnector();

	@Override
	public void beforeExecution() throws Exception
	{
		loadR2Project();
		connectToProjectDatabase();
	}

	private void loadR2Project() throws IOException
	{
		project = bjoernProjectConnector.getProject();
		String r2ProjectFilename = project.getR2ProjectFilename();
		String pathToBinary = project.getPathToBinary();
		radare.loadBinary(pathToBinary);
		radare.loadProject(r2ProjectFilename);
	}

	private void connectToProjectDatabase()
	{
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
