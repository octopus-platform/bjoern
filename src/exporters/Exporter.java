package exporters;

import exporters.outputModules.CSV.CSVOutputModule;
import exporters.radare.CommandLineInterface;

/**
 * Exporters extract information from binaries and make it available for later
 * import into the graph database. To achieve this, an input module is employed
 * to create logical objects (see package `structures`) from input binaries. An
 * output module subsequently makes logical objects available in an output
 * format.
 */

public abstract class Exporter
{
	protected InputModule inputModule;
	protected CSVOutputModule outputModule;
	protected CommandLineInterface cmdLine;

	public abstract void run(String[] args);

}
