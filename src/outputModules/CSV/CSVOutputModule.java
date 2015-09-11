package outputModules.CSV;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import outputModules.OutputModule;
import structures.BasicBlock;
import structures.CFGEdge;
import structures.Function;
import structures.Instruction;

public class CSVOutputModule implements OutputModule
{

	public void initialize()
	{
		CSVWriter.changeOutputDir("/tmp");
	}

	public void finish()
	{
		CSVWriter.finish();
	}

	public void writeFunction(Function function)
	{
		CSVWriter.reset();
		writeBasicBlocks(function);
		writeCFGEdges(function);
	}

	private void writeBasicBlocks(Function function)
	{
		Collection<BasicBlock> basicBlocks = function.getBasicBlocks();
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
		CSVWriter.addEdge(srcId, dstId, properties, "IS_BB_OF");
	}

	private void writeInstruction(BasicBlock block, Instruction instr,
			int childNum)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", block.getAddress().toString());
		properties.put("repr", instr.getStringRepr());
		properties.put("childNum", String.format("%d", childNum));
		CSVWriter.addNode(instr, properties);
	}

	private void writeNodeForBasicBlock(BasicBlock block)
	{
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", block.getAddress().toString());
		CSVWriter.addNode(block, properties);
	}

	private void writeCFGEdges(Function function)
	{
		List<CFGEdge> edges = function.getEdges();
		for (CFGEdge edge : edges)
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
