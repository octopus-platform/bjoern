package clients.bjoernImport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.ParseException;

// TODO: This code needs to be adapted to the changes introduced
// by the new architecture.

public class BjoernImport
{

	static CommandLineInterface cmdLine = new CommandLineInterface();

	public static void main(String[] args) throws MalformedURLException
	{
		parseCommandLine(args);
		invokeImportPlugin();
	}

	private static void invokeImportPlugin() throws MalformedURLException
	{

		String pathToBinary = cmdLine.getCodedir();
		pathToBinary = pathToBinary.replace("/", "|");

		try
		{
			URL url = new URL("http://localhost:2480/importcode/"
					+ pathToBinary);
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
