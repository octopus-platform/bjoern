package server.components.orientdbImporter.processors;

import java.io.IOException;

import server.components.orientdbImporter.CSVImporter;

import com.opencsv.CSVReader;

public class UnedgeProcessor extends CSVFileProcessor
{

	public UnedgeProcessor(CSVImporter importer)
	{
		super(importer);
	}

	@Override
	protected void processFirstRow(CSVReader csvReader, String[] row)
			throws IOException
	{
		String[] keys = rowToKeys(row);

	}

	@Override
	protected void processRow(String[] row)
	{
		if (row.length < 3)
			return;
	}

}
