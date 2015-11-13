package tools.server;

import java.io.IOException;

import tools.radareExporter.RadareExporter;

public class ImportRunnable implements Runnable
{

	private String pathToBinary;

	public ImportRunnable(String codedir)
	{
		this.pathToBinary = codedir;
	}

	@Override
	public void run()
	{
		pathToBinary = pathToBinary.replace("|", "/");

		RadareExporter.export(pathToBinary, ".");
		CSVImporter csvImporter = new CSVImporter();

		try
		{
			csvImporter.importCSVFiles("nodes.csv", "edges.csv");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
