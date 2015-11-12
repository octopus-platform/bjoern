package tools.bjoernImport;

import org.apache.commons.cli.ParseException;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class BjoernImport
{

	static CommandLineInterface cmdLine = new CommandLineInterface();
	private static OrientGraph graph;

	public static void main(String[] args)
	{
		parseCommandLine(args);
		connectToDatabase();
		invokeImportPlugin();
		closeDatabase();
	}

	private static void invokeImportPlugin()
	{

	}

	private static void connectToDatabase()
	{
		graph = new OrientGraph("remote:127.0.0.1/tempDB");
	}

	private static void closeDatabase()
	{
		graph.shutdown();
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
