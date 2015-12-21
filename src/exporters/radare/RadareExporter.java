package exporters.radare;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exporters.InputModule;
import exporters.nodeStore.NodeStore;
import exporters.outputModules.CSV.CSVOutputModule;
import exporters.radare.inputModule.RadareInputModule;
import exporters.structures.Flag;
import exporters.structures.Function;

public class RadareExporter
{

	private static final Logger logger = LoggerFactory
			.getLogger(RadareExporter.class);

	static CommandLineInterface cmdLine = new CommandLineInterface();
	static InputModule inputModule = new RadareInputModule();
	static CSVOutputModule outputModule = new CSVOutputModule();

	static List<Function> functions;

	public static void main(String[] args)
	{
		parseCommandLine(args);
		String binaryFilename = cmdLine.getBinaryFilename();
		String outputDir = cmdLine.getOutputDir();

		tryToExport(binaryFilename, outputDir);
	}

	private static void tryToExport(String binaryFilename, String outputDir)
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

	public static void export(String binaryFilename, String outputDir)
			throws IOException
	{

		logger.info("Exporting: {}", binaryFilename);

		inputModule.initialize(binaryFilename);
		outputModule.initialize(outputDir);
		loadAndOutputFlags();
		loadAndOutputFunctionInfo();
		loadAndOutputFunctionContent();
		outputModule.finish();
		inputModule.finish();
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

	private static void loadAndOutputFlags() throws IOException
	{
		List<Flag> flags = inputModule.getFlags();
		for (Flag flag : flags)
		{
			outputModule.writeFlag(flag);
		}
	}

	private static void loadAndOutputFunctionInfo() throws IOException
	{
		functions = inputModule.getFunctions();
		for (Function function : functions)
		{
			outputModule.writeFunctionInfo(function);
			outputModule.writeReferencesToFunction(function);
		}
	}

	private static void loadAndOutputFunctionContent() throws IOException
	{
		for (Function function : functions)
		{
			processFunction(function);
		}

	}

	private static void processFunction(Function function) throws IOException
	{

		if (function == null)
			return;

		inputModule.initializeFunctionContents(function);

		outputModule.writeFunctionContent(function);
		outputModule.writeUnresolvedContentEdges(function);

		// we clear the function content after writing it to free up some
		// memory. In addition, we clear all references to nodes still present
		// in caches.
		function.deleteContent();
		clearCaches();
	}

	private static void clearCaches()
	{
		NodeStore.clearCache();
	}

}
