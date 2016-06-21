package bjoern.input.common.outputModules.CSV;

import bjoern.input.common.outputModules.OutputModule;
import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeKey;
import bjoern.nodeStore.NodeTypes;
import bjoern.r2interface.creators.RadareInstructionCreator;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.RootNode;
import bjoern.structures.annotations.Flag;
import bjoern.structures.annotations.VariableOrArgument;
import bjoern.structures.edges.CallRef;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.edges.EdgeTypes;
import bjoern.structures.interpretations.*;

import java.util.*;

public class CSVOutputModule implements OutputModule
{

	Function currentFunction = null;

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

	@Override
	public void writeFlag(Flag flag)
	{
		createRootNodeForNode(flag);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(BjoernNodeProperties.CODE, flag.getValue());
		properties.put(BjoernNodeProperties.KEY, flag.getKey());
		properties.put(BjoernNodeProperties.TYPE, flag.getType());
		properties.put(BjoernNodeProperties.ADDR, flag.getAddress().toString());
		// Skipping length-field for now, let's see if we need it.
		CSVWriter.addNode(flag, properties);
	}

	private void createRootNodeForNode(Node node)
	{
		Node rootNode = new RootNode();
		rootNode.setAddr(node.getAddress());
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(BjoernNodeProperties.KEY, rootNode.getKey());
		properties.put(BjoernNodeProperties.ADDR, rootNode.getAddress().toString());
		properties.put(BjoernNodeProperties.TYPE, rootNode.getType());
		CSVWriter.addNoReplaceNode(rootNode, properties);
	}

	@Override
	public void writeFunctionNodes(Function function)
	{
		createRootNodeForNode(function);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(BjoernNodeProperties.ADDR, function.getAddress().toString());
		properties.put(BjoernNodeProperties.TYPE, function.getType());
		properties.put(BjoernNodeProperties.REPR, function.getName());
		properties.put(BjoernNodeProperties.KEY, function.getKey());

		CSVWriter.addNoReplaceNode(function, properties);
	}

	@Override
	public void writeFunctionContent(Function function)
	{
		setCurrentFunction(function);

		writeArgumentsAndVariables();
		writeBasicBlocks();
		writeCFGEdges();

		setCurrentFunction(null);
	}

	private void writeArgumentsAndVariables()
	{
		FunctionContent content = currentFunction.getContent();
		List<VariableOrArgument> varsAndArgs = content
				.getVariablesAndArguments();

		for (VariableOrArgument varOrArg : varsAndArgs)
		{
			createRootNodeForNode(varOrArg);
			createNodeForVarOrArg(varOrArg);
			addEdgeFromRootNode(varOrArg, EdgeTypes.ANNOTATION);
		}

	}

	private void createNodeForVarOrArg(VariableOrArgument varOrArg)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		String type = varOrArg.getType();
		if (type.equals(BjoernNodeProperties.VAR))
			properties.put(BjoernNodeProperties.TYPE, NodeTypes.LOCAL_VAR);
		else
			properties.put(BjoernNodeProperties.TYPE, NodeTypes.ARG);

		properties.put(BjoernNodeProperties.KEY, varOrArg.getKey());
		properties.put(BjoernNodeProperties.TYPE, varOrArg.getType());
		properties.put(BjoernNodeProperties.ADDR, varOrArg.getAddress().toString());
		properties.put(BjoernNodeProperties.NAME, varOrArg.getVarName());
		properties.put(BjoernNodeProperties.REPR, varOrArg.getVarType());
		properties.put(BjoernNodeProperties.CODE, varOrArg.getRegPlusOffset());

		CSVWriter.addNode(varOrArg, properties);
	}

	private void setCurrentFunction(Function function)
	{
		currentFunction = function;
	}

	private void writeBasicBlocks()
	{
		Function function = currentFunction;

		Collection<BasicBlock> basicBlocks = function.getContent()
				.getBasicBlocks();
		for (BasicBlock block : basicBlocks)
		{
			writeBasicBlock(block);
			writeEdgeFromFunctionToBasicBlock(function, block);
		}
	}

	private void writeEdgeFromFunctionToBasicBlock(Function function, BasicBlock block)
	{

		Map<String, Object> properties = new HashMap<String, Object>();

		String srcId = function.getKey();
		String dstId = block.getKey();

		CSVWriter.addEdge(srcId, dstId, properties, EdgeTypes.IS_FUNCTION_OF);

	}

	@Override
	public void writeBasicBlock(BasicBlock block)
	{
		createRootNodeForNode(block);
		writeNodeForBasicBlock(block);
		addEdgeFromRootNode(block, EdgeTypes.INTERPRETATION);
		writeInstructions(block);
	}

	private void writeInstructions(BasicBlock block)
	{
		Collection<Instruction> instructions = block.getInstructions();
		Iterator<Instruction> it = instructions.iterator();


		int childNum = 0;
		Instruction instr;
		while (it.hasNext())
		{
			instr = it.next();
			createRootNodeForNode(instr);
			writeInstruction(instr, childNum);
			addEdgeFromRootNode(instr, EdgeTypes.INTERPRETATION);

			writeEdgeFromBlockToInstruction(block, instr);
			childNum++;
		}

	}

	private void writeEdgeFromBlockToInstruction(BasicBlock block,
			Instruction instr)
	{
		Map<String, Object> properties = new HashMap<String, Object>();

		String srcId = block.getKey();
		String dstId = instr.getKey();

		CSVWriter.addEdge(srcId, dstId, properties, EdgeTypes.IS_BB_OF);
	}

	private void writeInstruction(Instruction instr,
			int childNum)
	{
		Map<String, Object> properties = new HashMap<String, Object>();

		Long instrAddress = instr.getAddress();

		properties.put(BjoernNodeProperties.ADDR, instrAddress.toString());
		properties.put(BjoernNodeProperties.TYPE, instr.getType());
		properties.put(BjoernNodeProperties.REPR, instr.getStringRepr());
		properties.put(BjoernNodeProperties.CHILD_NUM, String.format("%d", childNum));
		properties.put(BjoernNodeProperties.KEY, instr.getKey());
		properties.put(BjoernNodeProperties.CODE,instr.getBytes());

		addDisassemblyProperties(properties, instrAddress);

		CSVWriter.addNode(instr, properties);
	}

	private void addDisassemblyProperties(Map<String, Object> properties,
			Long address)
	{
		FunctionContent content = currentFunction.getContent();
		if (content == null)
			return;
		DisassemblyLine line = content.getDisassemblyLineForAddr(address);
		if (line == null)
			return;

		properties.put(BjoernNodeProperties.COMMENT, line.getComment());
		properties.put(BjoernNodeProperties.REPR, line.getInstruction());

		DisassemblyLine esilLine = content.getDisassemblyEsilLineForAddr(address);
		if (esilLine == null)
			return;

		properties.put(BjoernNodeProperties.ESIL, esilLine.getInstruction());

	}

	private void writeNodeForBasicBlock(BasicBlock block)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(BjoernNodeProperties.ADDR, block.getAddress().toString());
		properties.put(BjoernNodeProperties.TYPE, block.getType());
		properties.put(BjoernNodeProperties.KEY, block.getKey());
		properties.put(BjoernNodeProperties.REPR, block.getInstructionsStr());

		CSVWriter.addNode(block, properties);
	}

	private void writeCFGEdges()
	{
		Function function = currentFunction;
		List<DirectedEdge> edges = function.getContent().getEdges();
		for (DirectedEdge edge : edges)
		{

			NodeKey from = edge.getSourceKey();
			NodeKey to = edge.getDestKey();
			String srcId = from.toString();
			String dstId = to.toString();

			Map<String, Object> properties = new HashMap<String, Object>();
			String edgeType = edge.getType();
			CSVWriter.addEdge(srcId, dstId, properties, edgeType);
		}
	}

	@Override
	public void writeReferencesToFunction(Function function)
	{
		addEdgeFromRootNode(function, EdgeTypes.INTERPRETATION);
	}

	private void writeEdge(DirectedEdge edge)
	{

		String sourceKey = edge.getSourceKey().toString();
		String destKey = edge.getDestKey().toString();
		String type = edge.getType();
		Map<String, Object> properties = new HashMap<String, Object>();
		// TODO: add edge properties.
		CSVWriter.addEdge(sourceKey, destKey, properties, type);
	}

	@Override
	public void attachFlagsToRootNodes(Flag flag)
	{
		addEdgeFromRootNode(flag, EdgeTypes.ANNOTATION);
	}

	private void addEdgeFromRootNode(Node node, String type)
	{
		NodeKey srcKey = node.createEpsilonKey();
		NodeKey destKey = node.createKey();

		DirectedEdge newEdge = new DirectedEdge();
		newEdge.setSourceKey(srcKey);
		newEdge.setDestKey(destKey);
		newEdge.setType(type);

		writeEdge(newEdge);
	}

	public void writeCrossReference(DirectedEdge xref)
	{
		writeSourceNode(xref);
		writeEdge(xref);
	}

	private void writeSourceNode(DirectedEdge xref)
	{
		if(!(xref instanceof CallRef))
			return;

		CallRef callRef = (CallRef) xref;
		DisassemblyLine disassemblyLine = callRef.getDisassemblyLine();

		Instruction instruction = RadareInstructionCreator.createFromDisassemblyLine(disassemblyLine);

		Map<String, Object> properties = new HashMap<String, Object>();

		Long instrAddress = callRef.getSourceKey().getAddress();

		properties.put(BjoernNodeProperties.ADDR, instrAddress.toString());
		properties.put(BjoernNodeProperties.TYPE, instruction.getType());
		properties.put(BjoernNodeProperties.REPR, instruction.getStringRepr());
		properties.put(BjoernNodeProperties.KEY, instruction.getKey());
		properties.put(BjoernNodeProperties.CODE,instruction.getBytes());
		properties.put(BjoernNodeProperties.COMMENT, disassemblyLine.getComment());

		CSVWriter.addNode(instruction, properties);

	}

}
