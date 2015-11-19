package clients.bjoernImport;

import org.apache.commons.cli.ParseException;

import exporters.CommonCommandLineInterface;


public class CommandLineInterface extends CommonCommandLineInterface {
	String codedir;

	public String getCodedir() {
		return codedir;
	}

	public void printHelp() {
		formater.printHelp("importer <codedir> ...", options);
	}

	public void parseCommandLine(String[] args) throws ParseException {
		if (args.length != 1)
			throw new RuntimeException("Please supply a directory to import");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		codedir = arguments[0];
	}

}
