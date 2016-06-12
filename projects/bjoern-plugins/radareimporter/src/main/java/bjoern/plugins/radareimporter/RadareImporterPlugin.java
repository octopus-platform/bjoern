package bjoern.plugins.radareimporter;

import bjoern.input.radare.RadareExporter;
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
		String pathToBinary = getBjoernProjectConnector().getProject().getPathToBinary();
		String pathToProjectDir = getBjoernProjectConnector().getProject().getPathToProjectDir();
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
		String dbName = getBjoernProjectConnector().getProject().getDatabaseName();
		String nodeFilename = getBjoernProjectConnector().getProject().getNodeFilename();
		String edgeFilename = getBjoernProjectConnector().getProject().getEdgeFilename();
		return new ImportJob(nodeFilename, edgeFilename, dbName);
	}

}
