package exporters.outputModules.CSV;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeKey;
import exporters.nodeStore.NodeTypes;
import exporters.outputModules.OutputModule;
import exporters.structures.annotations.Flag;
import exporters.structures.annotations.VariableOrArgument;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.edges.EdgeTypes;
import exporters.structures.interpretations.BasicBlock;
import exporters.structures.interpretations.DisassemblyLine;
import exporters.structures.interpretations.Function;
import exporters.structures.interpretations.FunctionContent;
import exporters.structures.interpretations.Instruction;

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
		properties.put(CSVFields.CODE, flag.getValue());
		properties.put(CSVFields.KEY, flag.getKey());
		properties.put(CSVFields.TYPE, flag.getType());
		properties.put(CSVFields.ADDR, flag.getAddress().toString());
		// Skipping length-field for now, let's see if we need it.
		CSVWriter.addNode(flag, properties);
	}

	private void createRootNodeForNode(Node node)
	{
		Node rootNode = new Node();
		rootNode.setAddr(node.getAddress());
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CSVFields.KEY, rootNode.getKey());
		properties.put(CSVFields.ADDR, rootNode.getAddress().toString());
		CSVWriter.addNoReplaceNode(rootNode, properties);
	}

	@Override
	public void writeFunctionNodes(Function function)
	{
		createRootNodeForNode(function);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CSVFields.ADDR, function.getAddress().toString());
		properties.put(CSVFields.TYPE, function.getType());
		properties.put(CSVFields.REPR, function.getName());
		properties.put(CSVFields.KEY, function.getKey());

		CSVWriter.addNoReplaceNode(function, properties);
	}

	@Override
	public void writeFunctionContent(Function function)
	{
		setCurrentFunction(function);

		writeArgumentsAndVariables();
		writeBasicBlocks();
		writeCFGEdges();
		writeOtherEdges(function);

		setCurrentFunction(null);
	}

	private void writeOtherEdges(Function function)
	{

		FunctionContent content = function.getContent();

		List<DirectedEdge> edges = content.getUnresolvedEdges();
		for (DirectedEdge edge : edges)
		{
			writeEdge(edge);
		}

	}

	private void writeArgumentsAndVariables()
	{
		FunctionContent content = currentFunction.getContent();
		List<VariableOrArgument> varsAndArgs = content
				.getVariablesAndArguments();

		for (VariableOrArgument varOrArg : varsAndArgs)
		{
			createNodeForVarOrArg(varOrArg);
			connectNodeToFunction(varOrArg);
		}

	}

	private void connectNodeToFunction(VariableOrArgument varOrArg)
	{
		Function function = currentFunction;

		String srcId = varOrArg.getKey();
		String dstId = function.getKey();

		if (varOrArg.getType().equals(CSVFields.VAR))
			CSVWriter.addEdge(srcId, dstId, null, EdgeTypes.IS_VAR_OF);
		else
			CSVWriter.addEdge(srcId, dstId, null, EdgeTypes.IS_ARG_OF);
	}

	private void createNodeForVarOrArg(VariableOrArgument varOrArg)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		String type = varOrArg.getType();
		if (type.equals(CSVFields.VAR))
			properties.put(CSVFields.TYPE, NodeTypes.LOCAL_VAR);
		else
			properties.put(CSVFields.TYPE, NodeTypes.ARG);

		properties.put(CSVFields.KEY, varOrArg.getKey());
		properties.put(CSVFields.TYPE, varOrArg.getType());
		properties.put(CSVFields.ADDR, varOrArg.getAddress().toString());
		properties.put(CSVFields.NAME, varOrArg.getVarName());
		properties.put(CSVFields.REPR, varOrArg.getVarType());
		properties.put(CSVFields.CODE, varOrArg.getRegPlusOffset());

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
		}
	}

	@Override
	public void writeBasicBlock(BasicBlock block)
	{
		createRootNodeForNode(block);
		writeNodeForBasicBlock(block);
		addEdgeFromRootNode(block);
		writeInstructions(block);
	}

	private void writeInstructions(BasicBlock block)
	{
		Collection<Instruction> instructions = block.getInstructions();
		Iterator<Instruction> it = instructions.iterator();

		int childNum = 0;
		while (it.hasNext())
		{
			Instruction instr = it.next();
			createRootNodeForNode(instr);
			writeInstruction(block, instr, childNum);
			addEdgeFromRootNode(instr);
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

	private void writeInstruction(BasicBlock block, Instruction instr,
			int childNum)
	{
		Map<String, Object> properties = new HashMap<String, Object>();

		Long instrAddress = instr.getAddress();

		properties.put(CSVFields.ADDR, instrAddress.toString());
		properties.put(CSVFields.TYPE, instr.getType());
		properties.put(CSVFields.REPR, instr.getStringRepr());
		properties.put(CSVFields.CHILD_NUM, String.format("%d", childNum));
		properties.put(CSVFields.KEY, instr.getKey());

		String funcId = currentFunction.getKey();
		properties.put(CSVFields.FUNCTION_ID, funcId);

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
		properties.put(CSVFields.CODE, line.getInstruction());
		properties.put(CSVFields.COMMENT, line.getComment());
	}

	private void writeNodeForBasicBlock(BasicBlock block)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CSVFields.ADDR, block.getAddress().toString());
		properties.put(CSVFields.TYPE, block.getType());
		properties.put(CSVFields.KEY, block.getKey());

		String funcId = currentFunction.getKey();
		properties.put(CSVFields.FUNCTION_ID, funcId);

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
		List<DirectedEdge> edges = function.getEdges();
		for (DirectedEdge edge : edges)
		{
			writeEdge(edge);
		}

		addEdgeFromRootNode(function);
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
	public void writeReferenceToFlag(Flag flag)
	{
		addEdgeFromRootNode(flag);
	}

	private void addEdgeFromRootNode(Node node)
	{
		NodeKey srcKey = node.createEpsilonKey();
		NodeKey destKey = node.createKey();

		DirectedEdge newEdge = new DirectedEdge();
		newEdge.setSourceKey(srcKey);
		newEdge.setDestKey(destKey);

		writeEdge(newEdge);
	}

}
