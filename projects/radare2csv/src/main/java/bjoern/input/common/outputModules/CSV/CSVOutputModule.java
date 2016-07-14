package bjoern.input.common.outputModules.CSV;

import bjoern.input.common.outputModules.OutputModule;
import bjoern.nodeStore.Node;
import bjoern.structures.RootNode;
import bjoern.structures.annotations.Flag;
import bjoern.structures.annotations.VariableOrArgument;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.edges.EdgeTypes;
import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Function;
import bjoern.structures.interpretations.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVOutputModule implements OutputModule
{

	@Override
	public void initialize(String outputDir)
	{
		CSVWriter.changeOutputDir(outputDir);
	}

	@Override
	public void finish()
	{
		CSVWriter.finish();
	}

	private void writeEdge(DirectedEdge edge)
	{
		String sourceKey = edge.getSourceKey().toString();
		String destKey = edge.getDestKey().toString();
		String label = edge.getType();
		Map<String, Object> properties = new HashMap<>();
		// TODO: add edge properties.
		CSVWriter.addEdge(sourceKey, destKey, properties, label);
	}

	private void writeEdgeBetweenNodes(Node source, Node destination, String label)
	{
		CSVWriter.addEdge(source.getKey(), destination.getKey(), null, label);
	}

	private void writeNodeNoReplace(Node node)
	{
		CSVWriter.addNoReplaceNode(node);
	}

	private void writeNode(Node node)
	{
		CSVWriter.addNode(node);
	}

	private void writeRootNodeAndEdgeForNode(Node node, String edgeType)
	{
		RootNode rootNode = new RootNode(node.getAddress());
		writeNodeNoReplace(rootNode);
		writeEdgeBetweenNodes(rootNode, node, edgeType);
	}

	@Override
	public void writeFunction(Function function)
	{
		writeNodeNoReplace(function);
		writeRootNodeAndEdgeForNode(function, EdgeTypes.INTERPRETATION);
		writeBasicBlocksOfFunction(function);
		writeArgumentsAndVariablesOfFunction(function);
		writeCFGEdges(function);
	}

	@Override
	public void writeFlag(Flag flag)
	{
		writeNode(flag);
		writeRootNodeAndEdgeForNode(flag, EdgeTypes.ANNOTATION);
	}

	@Override
	public void writeCrossReference(DirectedEdge xref)
	{
		writeEdge(xref);
	}

	private void writeBasicBlocksOfFunction(Function function)
	{
		for (BasicBlock block : function.getContent().getBasicBlocks())
		{
			writeNode(block);
			writeRootNodeAndEdgeForNode(block, EdgeTypes.INTERPRETATION);
			writeEdgeFromFunctionToBasicBlock(function, block);
			writeInstructionsOfBasicBlock(block);
		}
	}

	private void writeInstructionsOfBasicBlock(BasicBlock block)
	{
		for (Instruction instruction : block.getInstructions())
		{
			writeNode(instruction);
			writeRootNodeAndEdgeForNode(instruction, EdgeTypes.INTERPRETATION);
			writeEdgeFromBlockToInstruction(block, instruction);
		}
	}

	private void writeArgumentsAndVariablesOfFunction(Function function)
	{
		for (VariableOrArgument varOrArg : function.getContent().getVariablesAndArguments())
		{
			writeNode(varOrArg);
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
		List<DirectedEdge> edges = function.getContent().getEdges();
		for (DirectedEdge edge : edges)
		{

			writeEdge(edge);
		}
	}

}
