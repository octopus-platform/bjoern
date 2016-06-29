package bjoern.pluginlib.plugintypes;

import java.io.IOException;

import bjoern.pluginlib.BjoernProject;
import bjoern.r2interface.Radare;
import octopus.lib.connectors.OrientDBConnector;

public abstract class RadareProjectPlugin extends BjoernProjectPlugin
{

	private Radare radare = new Radare();
	private BjoernProject project;
	private OrientDBConnector orientConnector = new OrientDBConnector();

	@Override
	public void beforeExecution() throws Exception
	{
		loadR2Project();
		connectToProjectDatabase();
	}

	private void loadR2Project() throws IOException
	{
		setProject((BjoernProject) getBjoernProjectConnector().getWrapper());
		String r2ProjectFilename = getProject().getR2ProjectFilename();
		String pathToBinary = getProject().getPathToBinary();
		getRadare().loadBinary(pathToBinary);
		getRadare().loadProject(r2ProjectFilename);
	}

	private void connectToProjectDatabase()
	{
		String databaseName = getProject().getDatabaseName();
		getOrientConnector().connect(databaseName);
	}

	@Override
	public void afterExecution() throws Exception
	{
		getOrientConnector().disconnect();
		getRadare().shutdown();
	}

	protected Radare getRadare()
	{
		return radare;
	}

	protected void setRadare(Radare radare)
	{
		this.radare = radare;
	}

	protected BjoernProject getProject()
	{
		return project;
	}

	protected void setProject(BjoernProject project)
	{
		this.project = project;
	}

	protected OrientDBConnector getOrientConnector()
	{
		return orientConnector;
	}

	protected void setOrientConnector(OrientDBConnector orientConnector)
	{
		this.orientConnector = orientConnector;
	}

}
