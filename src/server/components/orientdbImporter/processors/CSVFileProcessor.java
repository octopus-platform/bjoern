package server.components.orientdbImporter.processors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import server.components.orientdbImporter.CSVImporter;

import com.opencsv.CSVReader;

public abstract class CSVFileProcessor
{
	protected final CSVImporter importer;

	public CSVFileProcessor(CSVImporter importer)
	{
		this.importer = importer;
	}

	public void process(String filename) throws IOException
	{
		CSVReader csvReader = getCSVReaderForFile(filename);

		processFirstRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processRow(row);
		}
	}

	protected abstract void processFirstRow(CSVReader csvReader)
			throws IOException;

	protected abstract void processRow(String[] row);

	private CSVReader getCSVReaderForFile(String filename)
			throws FileNotFoundException
	{
		CSVReader reader;
		FileReader fileReader = new FileReader(filename);
		reader = new CSVReader(fileReader, '\t');
		return reader;
	}

}
