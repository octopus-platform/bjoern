package exporters.radare;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;

import exporters.Exporter;
import exporters.nodeStore.NodeStore;
import exporters.outputModules.CSV.CSVOutputModule;
import exporters.radare.inputModule.RadareInputModule;
import exporters.structures.Flag;
import exporters.structures.Function;

public class RadareExporter extends Exporter
{

	List<Function> functions;

	@Override
	public void run(String[] args)
	{
		initialize();
		parseCommandLine(args);
		String binaryFilename = cmdLine.getBinaryFilename();
		String outputDir = cmdLine.getOutputDir();

		tryToExport(binaryFilename, outputDir);
	}

	private void initialize()
	{
		cmdLine = new CommandLineInterface();
		inputModule = new RadareInputModule();
		outputModule = new CSVOutputModule();
	}

	private void parseCommandLine(String[] args)
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

	private void printHelpAndTerminate(Exception e)
	{
		System.err.println(e.getMessage());
		cmdLine.printHelp();
		System.exit(0);
	}

	@Override
	protected void loadAndOutput() throws IOException
	{
		loadAndOutputFlags();
		loadAndOutputFunctionInfo();
		loadAndOutputFunctionContent();
	}

	private void loadAndOutputFlags() throws IOException
	{
		List<Flag> flags = inputModule.getFlags();
		for (Flag flag : flags)
		{
			outputModule.writeFlag(flag);
		}
	}

	private void loadAndOutputFunctionInfo() throws IOException
	{
		functions = inputModule.getFunctions();
		for (Function function : functions)
		{
			outputModule.writeFunctionInfo(function);
			outputModule.writeReferencesToFunction(function);
		}
	}

	private void loadAndOutputFunctionContent() throws IOException
	{
		for (Function function : functions)
		{
			processFunction(function);
		}

	}

	private void processFunction(Function function) throws IOException
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

	private void clearCaches()
	{
		NodeStore.clearCache();
	}

}
