package server.commands.importcsv;

import java.io.IOException;

import com.orientechnologies.common.log.OLogManager;

public class ImportCSVRunnable implements Runnable
{

	private final GraphFiles graphFiles;

	public ImportCSVRunnable(GraphFiles graphFiles)
	{
		this.graphFiles = graphFiles;
	}

	@Override
	public void run()
	{

		CSVImporter csvImporter = new CSVImporter();

		String nodeFilename = graphFiles.getNodeFilename();
		String edgeFilename = graphFiles.getEdgeFilename();

		OLogManager.instance().warn(this, nodeFilename);

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
