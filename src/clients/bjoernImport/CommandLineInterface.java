package clients.bjoernImport;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import server.Constants;
import exporters.CommonCommandLineInterface;

public class CommandLineInterface extends CommonCommandLineInterface
{
	String codedir;
	private String dbName = Constants.DEFAULT_DB_NAME;

	public CommandLineInterface()
	{
		super();
	}

	public String getCodedir()
	{
		return codedir;
	}

	public String getDbName()
	{
		return dbName;
	}

	@Override
	public void initializeOptions()
	{
		Option dbName = OptionBuilder.withArgName("dbname").hasArg()
				.withDescription("name of the database to import into.")
				.create("dbname");

		options.addOption(dbName);
	}

	public void printHelp()
	{
		formater.printHelp("importer <codedir> ...", options);
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length == 0)
			throw new RuntimeException("Please supply a directory to import");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		codedir = arguments[0];

		if (cmd.hasOption("dbname"))
			dbName = cmd.getOptionValue("dbname");

	}

}
