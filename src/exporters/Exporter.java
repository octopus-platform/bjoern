package exporters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory
			.getLogger(Exporter.class);

	public abstract void run(String[] args);

	public void tryToExport(String binaryFilename, String outputDir)
	{
		try
		{
			export(binaryFilename, outputDir);
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public void export(String binaryFilename, String outputDir)
			throws IOException
	{

		logger.info("Exporting: {}", binaryFilename);

		inputModule.initialize(binaryFilename);
		outputModule.initialize(outputDir);
		loadAndOutput();
		outputModule.finish();
		inputModule.finish();
	}

	protected abstract void loadAndOutput() throws IOException;

}
