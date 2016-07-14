package bjoern.input.radare;

import bjoern.input.common.Exporter;
import bjoern.input.common.outputModules.CSV.CSVOutputModule;
import bjoern.input.common.outputModules.OutputModule;
import bjoern.input.radare.inputModule.RadareInputModule;
import bjoern.nodeStore.Node;
import bjoern.structures.RootNode;
import bjoern.structures.annotations.Flag;
import bjoern.structures.annotations.VariableOrArgument;
import bjoern.structures.edges.CallRef;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.edges.EdgeTypes;
import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.Instruction;

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
		loadAndOutputFunctions();
		loadAndOutputCrossReferences();
	}

	private void loadAndOutputCrossReferences()
	{
		try
		{
			for (CallRef call : getInputModule().getCallReferences())
			{
				getOutputModule().writeEdge(call);
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
			writeFlag(flag);
		}
	}

	private void loadAndOutputFunctions() throws IOException
	{
		functions = getInputModule().getFunctions();
		for (Function function : functions)
		{
			writeFunction(function);

			// we clear the function content after writing it to free up some
			// memory. In addition, we clear all references to nodes still present
			// in caches.
			function.deleteContent();
		}
	}

	public void writeFunction(Function function)
	{
		getOutputModule().writeNodeNoReplace(function);
		writeRootNodeAndEdgeForNode(function, EdgeTypes.INTERPRETATION);
		writeBasicBlocksOfFunction(function);
		writeArgumentsAndVariablesOfFunction(function);
		writeCFGEdges(function);
	}

	public void writeFlag(Flag flag)
	{
		getOutputModule().writeNode(flag);
		writeRootNodeAndEdgeForNode(flag, EdgeTypes.ANNOTATION);
	}

	private void writeEdgeBetweenNodes(Node source, Node destination, String label)
	{
		getOutputModule().writeEdge(new DirectedEdge(source.createKey(), destination.createKey(), label));
	}

	private void writeRootNodeAndEdgeForNode(Node node, String edgeType)
	{
		RootNode rootNode = new RootNode(node.getAddress());
		getOutputModule().writeNodeNoReplace(rootNode);
		writeEdgeBetweenNodes(rootNode, node, edgeType);
	}

	private void writeBasicBlocksOfFunction(Function function)
	{
		for (BasicBlock block : function.getContent().getBasicBlocks())
		{
			getOutputModule().writeNode(block);
			writeRootNodeAndEdgeForNode(block, EdgeTypes.INTERPRETATION);
			writeEdgeFromFunctionToBasicBlock(function, block);
			writeInstructionsOfBasicBlock(block);
		}
	}

	private void writeInstructionsOfBasicBlock(BasicBlock block)
	{
		for (Instruction instruction : block.getInstructions())
		{
			getOutputModule().writeNode(instruction);
			writeRootNodeAndEdgeForNode(instruction, EdgeTypes.INTERPRETATION);
			writeEdgeFromBlockToInstruction(block, instruction);
		}
	}

	private void writeArgumentsAndVariablesOfFunction(Function function)
	{
		for (VariableOrArgument varOrArg : function.getContent().getVariablesAndArguments())
		{
			getOutputModule().writeNode(varOrArg);
			writeRootNodeAndEdgeForNode(varOrArg, EdgeTypes.ANNOTATION);
		}
	}

	private void writeEdgeFromFunctionToBasicBlock(Function function, BasicBlock block)
	{
		writeEdgeBetweenNodes(function, block, EdgeTypes.IS_FUNCTION_OF);
	}

	private void writeEdgeFromBlockToInstruction(BasicBlock block,
			Instruction instr)
	{
		writeEdgeBetweenNodes(block, instr, EdgeTypes.IS_BB_OF);
	}

	private void writeCFGEdges(Function function)
	{
		List<DirectedEdge> edges = function.getContent().getControlFlowEdges();
		for (DirectedEdge edge : edges)
		{
			getOutputModule().writeEdge(edge);
		}
	}

}
