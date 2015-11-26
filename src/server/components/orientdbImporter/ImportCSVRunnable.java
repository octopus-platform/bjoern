package server.components.orientdbImporter;

import java.io.IOException;

import server.DebugPrinter;

public class ImportCSVRunnable implements Runnable
{

	private final ImportJob importJob;

	public ImportCSVRunnable(ImportJob importJob)
	{
		this.importJob = importJob;
	}

	@Override
	public void run()
	{

		CSVBatchImporter csvBatchImporter = new CSVBatchImporter();
		CSVLookupImporter csvLookupImporter = new CSVLookupImporter();

		String nodeFilename = importJob.getNodeFilename();
		String edgeFilename = importJob.getEdgeFilename();
		String unedgeFilename = importJob.getUnedgeFilename();
		String dbName = importJob.getDbName();

		try
		{
			csvBatchImporter.setDbName(dbName);
			csvBatchImporter.importCSVFiles(nodeFilename, edgeFilename);

			csvLookupImporter.setDbName(dbName);
			csvLookupImporter.importCSVFiles(null, unedgeFilename);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		DebugPrinter.print("Import finished", this);
	}

}
