package tools.radareExporter;

import inputModules.InputModule;
import inputModules.radare.RadareInputModule;

import java.util.List;

import nodeStore.NodeStore;

import org.apache.commons.cli.ParseException;

import outputModules.CSV.CSVOutputModule;
import structures.Function;

public class Main
{

	static CommandLineInterface cmdLine = new CommandLineInterface();
	static InputModule inputModule = new RadareInputModule();
	static CSVOutputModule outputModule = new CSVOutputModule();

	static List<Function> functions;

	public static void main(String[] args)
	{
		parseCommandLine(args);
		String binaryFilename = cmdLine.getBinaryFilename();
		String outputDir = cmdLine.getOutputDir();

		inputModule.initialize(binaryFilename);
		outputModule.initialize(outputDir);

		loadAndOutputFunctionInfo();
		loadAndOutputFunctionContent();

		outputModule.finish();
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

	private static void loadAndOutputFunctionInfo()
	{
		functions = inputModule.getFunctions();
		for (Function function : functions)
		{
			outputModule.writeFunctionInfo(function);
			outputModule.writeReferencesToFunction(function);
		}
	}

	private static void loadAndOutputFunctionContent()
	{
		for (Function function : functions)
		{
			processFunction(function);
		}

	}

	private static void processFunction(Function function)
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
		outputModule.clearCache();
	}

}
