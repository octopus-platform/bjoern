package tools.radareExporter;

import org.apache.commons.cli.ParseException;

import tools.CommonCommandLineInterface;

public class CommandLineInterface extends CommonCommandLineInterface
{

	private String binaryFilename;

	public String getBinaryFilename()
	{
		return binaryFilename;
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length != 1)
			throw new RuntimeException("Please supply a file to process");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		binaryFilename = arguments[0];
	}

	public void printHelp()
	{
		formater.printHelp("exporter <filename>", options);
	}

}
