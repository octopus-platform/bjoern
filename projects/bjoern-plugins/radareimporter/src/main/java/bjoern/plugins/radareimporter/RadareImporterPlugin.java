package bjoern.plugins.radareimporter;

import bjoern.input.radare.RadareExporter;
import bjoern.pluginlib.BjoernProject;
import bjoern.pluginlib.plugintypes.BjoernProjectPlugin;
import octopus.server.components.orientdbImporter.ImportCSVRunnable;
import octopus.server.components.orientdbImporter.ImportJob;

public class RadareImporterPlugin extends BjoernProjectPlugin {

	@Override
	public void execute() throws Exception
	{
		raiseIfDatabaseForProjectExists();
		extractCSVFilesFromBinary();
		importCSVFilesIntoDatabase();
	}

	private void extractCSVFilesFromBinary()
	{
		BjoernProject bjoernProject = (BjoernProject) getProjectConnector().getWrapper();

		String pathToBinary = bjoernProject.getPathToBinary();
		String pathToProjectDir = bjoernProject.getPathToProjectDir();
		RadareExporter radareExporter = new RadareExporter();
		radareExporter.tryToExport(pathToBinary, pathToProjectDir, null);
	}

	private void importCSVFilesIntoDatabase()
	{
		ImportJob importJob = createImportJobForProject();
		(new ImportCSVRunnable(importJob)).run();
	}

	private ImportJob createImportJobForProject()
	{
		BjoernProject bjoernProject = (BjoernProject) getProjectConnector().getWrapper();

		String dbName = bjoernProject.getDatabaseName();
		String nodeFilename = bjoernProject.getNodeFilename();
		String edgeFilename = bjoernProject.getEdgeFilename();
		return new ImportJob(nodeFilename, edgeFilename, dbName);
	}

}
