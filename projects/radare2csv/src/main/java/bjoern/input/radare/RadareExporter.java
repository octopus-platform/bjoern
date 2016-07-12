package bjoern.input.radare;

import bjoern.input.common.Exporter;
import bjoern.input.common.outputModules.CSV.CSVOutputModule;
import bjoern.input.common.outputModules.OutputModule;
import bjoern.input.radare.inputModule.RadareInputModule;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.edges.Reference;
import bjoern.structures.interpretations.Function;

import java.io.IOException;
import java.util.List;

/**
 * The Radare exporter uses the disassembly framework radare2
 * to extract graph representations of code from binaries.
 * <p>
 * In its current version, it simply executes "analyze all"
 * on the binary and writes out the resulting information
 * in CSV format. In the future, we would like to process
 * radare2 project files instead, so that any edits users have
 * made to the disassembly can be accounted for.
 */

public class RadareExporter extends Exporter
{

	List<Function> functions;

	public RadareExporter()
	{
		this(new CSVOutputModule());
	}

	public RadareExporter(OutputModule outputModule)
	{
		super(new RadareInputModule(), outputModule);
	}

	@Override
	public RadareInputModule getInputModule()
	{
		return (RadareInputModule) super.getInputModule();
	}


	@Override
	protected void export() throws IOException
	{
		loadAndOutputFlags();
		loadAndOutputFunctionInfo();
		loadAndOutputFunctionContent();
		loadAndOutputCrossReferences();
	}

	private void loadAndOutputCrossReferences()
	{
		List<Reference> references;
		try
		{
			references = getInputModule().getCrossReferences();
			for (DirectedEdge xref : references)
			{
				getOutputModule().writeCrossReference(xref);
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadAndOutputFlags() throws IOException
	{
		List<Flag> flags = getInputModule().getFlags();
		for (Flag flag : flags)
		{
			getOutputModule().writeFlag(flag);
			getOutputModule().attachFlagsToRootNodes(flag);
		}
	}

	private void loadAndOutputFunctionInfo() throws IOException
	{
		functions = getInputModule().getFunctions();
		for (Function function : functions)
		{
			getOutputModule().writeFunctionNodes(function);
			getOutputModule().writeReferencesToFunction(function);
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

		getInputModule().initializeFunctionContents(function);
		getOutputModule().writeFunctionContent(function);

		// we clear the function content after writing it to free up some
		// memory. In addition, we clear all references to nodes still present
		// in caches.
		function.deleteContent();
	}

}
