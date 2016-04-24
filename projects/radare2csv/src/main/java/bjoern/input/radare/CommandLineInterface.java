package bjoern.input.radare;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

public class CommandLineInterface extends CommonCommandLineInterface
{

	private String binaryFilename;
	private String outputDir = ".";
	private String projectFilename;

	@Override
	protected void initializeOptions()
	{
		super.initializeOptions();

		Option outputDirectory = OptionBuilder.withArgName("outdir").hasArg()
				.withDescription("the directory the output will be written to")
				.create("outdir");

		options.addOption(outputDirectory);
	}

	public String getBinaryFilename()
	{
		return binaryFilename;
	}

	public String getProjectFilename()
	{
		return projectFilename;
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length == 0)
			throw new RuntimeException("Please supply a file to process");

		cmd = parser.parse(options, args);

		if (cmd.hasOption("outdir"))
			outputDir = cmd.getOptionValue("outdir");

		String[] arguments = cmd.getArgs();
		binaryFilename = arguments[0];
		if(arguments.length > 1)
			projectFilename = arguments[1];

	}

	public void printHelp()
	{
		formater.printHelp("exporter <filename>", options);
	}

	public String getOutputDir()
	{
		return outputDir;
	}

}
