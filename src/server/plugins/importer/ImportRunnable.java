package server.plugins.importer;

import java.io.IOException;

public class ImportRunnable implements Runnable
{

	private final GraphFiles graphFiles;

	public ImportRunnable(GraphFiles graphFiles)
	{
		this.graphFiles = graphFiles;
	}

	@Override
	public void run()
	{

		CSVImporter csvImporter = new CSVImporter();

		String nodeFilename = graphFiles.getNodeFilename();
		String edgeFilename = graphFiles.getEdgeFilename();

		try
		{
			csvImporter.importCSVFiles(nodeFilename, edgeFilename);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
