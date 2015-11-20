package clients.bjoernImport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.ParseException;

import exporters.radare.RadareExporter;

// TODO: This code needs to be adapted to the changes introduced
// by the new architecture.

public class BjoernImport
{

	static CommandLineInterface cmdLine = new CommandLineInterface();

	public static void main(String[] args) throws MalformedURLException
	{
		parseCommandLine(args);
		invokeRadare2(args);
		invokeImportPlugin();
	}

	private static void invokeRadare2(String[] args)
	{
		String pathToBinary = cmdLine.getCodedir();
		RadareExporter.export(pathToBinary, ".");
	}

	private static void invokeImportPlugin() throws MalformedURLException
	{

		try
		{
			String urlStr = String.format(
					"http://localhost:2480/importcsv/%s/%s", "nodes.csv",
					"edges.csv");

			URL url = new URL(urlStr);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(0);
			connection.getInputStream();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
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
