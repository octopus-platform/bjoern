package octopus.clients.importer;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import octopus.clients.CommonCommandLineInterface;
import octopus.server.Constants;

public class CommandLineInterface extends CommonCommandLineInterface
{
	private String dbName = Constants.DEFAULT_DB_NAME;

	public CommandLineInterface()
	{
		super();
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
		formater.printHelp("importer", options);
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		cmd = parser.parse(options, args);

		if (cmd.hasOption("dbname"))
			dbName = cmd.getOptionValue("dbname");

	}

}
