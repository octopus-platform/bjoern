package clients.bjoernImport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.ParseException;

import exporters.radare.RadareExporter;

public class BjoernImport
{

	static CommandLineInterface cmdLine = new CommandLineInterface();

	public static void main(String[] args) throws IOException
	{
		parseCommandLine(args);
		invokeRadare2(args);
		invokeImportPlugin();
	}

	private static void invokeRadare2(String[] args) throws IOException
	{
		String pathToBinary = cmdLine.getCodedir();
		RadareExporter.export(pathToBinary, ".");
	}

	private static void invokeImportPlugin() throws MalformedURLException
	{

		try
		{
			String workingDirectory = System.getProperty("user.dir");

			Path nodePath = Paths.get(workingDirectory, "nodes.csv");
			String nodeFilename = URLEncoder.encode(nodePath.toAbsolutePath()
					.toString());

			Path edgePath = Paths.get(workingDirectory, "edges.csv");
			String edgeFilename = URLEncoder.encode(edgePath.toAbsolutePath()
					.toString());

			String dbName = URLEncoder.encode(cmdLine.getDbName());

			String urlStr = String.format(
					"http://localhost:2480/importcsv/%s/%s/%s/", nodeFilename,
					edgeFilename, dbName);

			URL url = new URL(urlStr);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(0);
			connection.getInputStream();
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
