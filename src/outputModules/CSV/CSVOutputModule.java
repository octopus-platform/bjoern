package outputModules.CSV;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import outputModules.OutputModule;
import structures.BasicBlock;
import structures.Function;
import structures.Instruction;
import structures.edges.DirectedEdge;
import structures.edges.EdgeTypes;
import structures.edges.ResolvedCFGEdge;
import unresolvedEdgeStore.UnresolvedEdgeStore;

public class CSVOutputModule implements OutputModule
{

	public void initialize()
	{
		CSVWriter.changeOutputDir("/tmp");
	}

	@Override
	public void clearCache()
	{
		CSVWriter.clear();
	}

	public void finish()
	{
		CSVWriter.finish();
	}

	@Override
	public void writeFunctionInfo(Function function)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", function.getAddress().toString());
		properties.put("type", function.getType());
		properties.put("repr", function.getName());
		CSVWriter.addNode(function, properties);
	}

	public void writeFunctionContent(Function function)
	{
		writeBasicBlocks(function);
		writeCFGEdges(function);
	}

	private void writeBasicBlocks(Function function)
	{
		Collection<BasicBlock> basicBlocks = function.getContent()
				.getBasicBlocks();
		for (BasicBlock block : basicBlocks)
		{
			writeBasicBlock(block);
		}
	}

	public void writeBasicBlock(BasicBlock block)
	{
		writeNodeForBasicBlock(block);
		writeInstructions(block);
	}

	@Override
	public void writeUnresolvedEdges()
	{
		List<DirectedEdge> edges = UnresolvedEdgeStore.getEdges();
		for (DirectedEdge edge : edges)
		{
			String sourceKey = edge.getSourceNode().getKey();
			String destKey = edge.getDestNode().getKey();
			String type = edge.getType();

		}

		UnresolvedEdgeStore.clearCache();
	}

	private void writeInstructions(BasicBlock block)
	{
		Collection<Instruction> instructions = block.getInstructions();
		Iterator<Instruction> it = instructions.iterator();

		int childNum = 0;
		while (it.hasNext())
		{
			Instruction instr = it.next();
			writeInstruction(block, instr, childNum);
			writeEdgeFromBlockToInstruction(block, instr);
			childNum++;
		}

	}

	private void writeEdgeFromBlockToInstruction(BasicBlock block,
			Instruction instr)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		long srcId = CSVWriter.getIdForNode(block);
		long dstId = CSVWriter.getIdForNode(instr);
		CSVWriter.addEdge(srcId, dstId, properties, EdgeTypes.IS_BB_OF);
	}

	private void writeInstruction(BasicBlock block, Instruction instr,
			int childNum)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", block.getAddress().toString());
		properties.put("type", instr.getType());
		properties.put("repr", instr.getStringRepr());
		properties.put("childNum", String.format("%d", childNum));
		CSVWriter.addNode(instr, properties);
	}

	private void writeNodeForBasicBlock(BasicBlock block)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", block.getAddress().toString());
		properties.put("type", block.getType());
		CSVWriter.addNode(block, properties);
	}

	private void writeCFGEdges(Function function)
	{
		List<ResolvedCFGEdge> edges = function.getContent().getEdges();
		for (ResolvedCFGEdge edge : edges)
		{
			BasicBlock from = edge.getFrom();
			BasicBlock to = edge.getTo();

			long srcId = CSVWriter.getIdForNode(from);
			long dstId = CSVWriter.getIdForNode(to);
			Map<String, Object> properties = new HashMap<String, Object>();
			String edgeType = edge.getType();
			CSVWriter.addEdge(srcId, dstId, properties, edgeType);
		}
	}

}
