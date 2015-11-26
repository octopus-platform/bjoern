package server.components.orientdbImporter;

import java.io.IOException;

import server.components.orientdbImporter.processors.KeyedEdgeProcessor;

public class CSVKeyedEdgeImporter extends CSVImporter
{

	@Override
	protected void openDatabase() throws IOException
	{
		openNoTxForMassiveInsert();
		graph = noTx;
	}

	@Override
	protected void processNodeFile(String nodeFile) throws IOException
	{
		// We currently only require lookup-based imports for edges,
		// so there's no code here.
	}

	@Override
	protected void processEdgeFile(String filename) throws IOException
	{
		if (filename == null)
			return;
		(new KeyedEdgeProcessor(this)).process(filename);
	}

}
