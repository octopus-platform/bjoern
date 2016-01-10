package exporters.radare;

import java.io.IOException;
import java.util.List;

import exporters.Exporter;
import exporters.nodeStore.NodeStore;
import exporters.outputModules.CSV.CSVOutputModule;
import exporters.radare.inputModule.RadareInputModule;
import exporters.structures.annotations.Flag;
import exporters.structures.interpretations.Function;

/**
 * The Radare exporter uses the disassembly framework radare2
 * to extract graph representations of code from binaries.
 *
 * In its current version, it simply executes "analyze all"
 * on the binary and writes out the resulting information
 * in CSV format. In the future, we would like to process
 * radare2 project files instead, so that any edits users have
 * made to the disassembly can be accounted for.
 * */

public class RadareExporter extends Exporter
{

	List<Function> functions;

	@Override
	protected void initialize()
	{
		cmdLine = new CommandLineInterface();
		inputModule = new RadareInputModule();
		outputModule = new CSVOutputModule();
	}

	@Override
	protected void export() throws IOException
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
			outputModule.writeFunctionNodes(function);
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
