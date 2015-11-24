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
	protected void processFirstRow(CSVReader csvReader) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void processRow(String[] row)
	{
		// TODO Auto-generated method stub

	}

}
