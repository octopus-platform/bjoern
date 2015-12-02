package server.components.orientdbImporter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportCSVRunnable implements Runnable
{

	private static final Logger logger = LoggerFactory
			.getLogger(ImportCSVRunnable.class);

	private final ImportJob importJob;

	public ImportCSVRunnable(ImportJob importJob)
	{
		this.importJob = importJob;
	}

	@Override
	public void run()
	{

		CSVBatchImporter csvBatchImporter = new CSVBatchImporter();
		CSVKeyedEdgeImporter csvKeyedEdgeImporter = new CSVKeyedEdgeImporter();

		String nodeFilename = importJob.getNodeFilename();
		String edgeFilename = importJob.getEdgeFilename();
		String keyedEdgeFilename = importJob.getKeyedEdgeFilename();
		String dbName = importJob.getDbName();

		try
		{
			csvBatchImporter.setDbName(dbName);
			csvBatchImporter.importCSVFiles(nodeFilename, edgeFilename);

			csvKeyedEdgeImporter.setDbName(dbName);
			csvKeyedEdgeImporter.importCSVFiles(null, keyedEdgeFilename);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		logger.info("Import finished");
	}

}
