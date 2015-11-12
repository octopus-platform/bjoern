package tools.bjoernImport;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

public class BjoernImport
{

	static CommandLineInterface cmdLine = new CommandLineInterface();

	public static void main(String[] args)
	{

		parseCommandLine(args);

		CSVImporter importer = new CSVImporter();

		try
		{
			importer.importCSVFiles(cmdLine.getNodeFile(),
					cmdLine.getEdgeFile());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void parseCommandLine(String[] args)
	{
		try
		{
			cmdLine.parseCommandLine(args);
		}
		catch (RuntimeException | ParseException e)
		{
			printHelpAndTerminate(e);
		}
	}

	private static void printHelpAndTerminate(Exception e)
	{
		System.err.println(e.getMessage());
		cmdLine.printHelp();
		System.exit(0);
	}

}
