package outputModules.CSV;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import outputModules.OutputModule;
import structures.BasicBlock;
import structures.CFGEdge;
import structures.Function;

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
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("addr", block.getAddr().toString());
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
