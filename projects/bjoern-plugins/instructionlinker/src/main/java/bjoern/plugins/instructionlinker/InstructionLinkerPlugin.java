package bjoern.plugins.instructionlinker;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.GraphOperations;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

public class InstructionLinkerPlugin extends OrientGraphConnectionPlugin
{
	private OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = orientConnector.getNoTxGraphInstance();

		Iterable<Function> functions = LookupOperations.getFunctions(graph);

		for (Function function : functions)
		{
			for (BasicBlock block : function.basicBlocks())
			{
				linkInstructions(block);
				for (BasicBlock nextBasicBlock : block.cflow())
				{
					linkBlocks(block, nextBasicBlock);
				}
			}
		}

		graph.shutdown();
	}

	/**
	 * Link the last instruction of block srcBlock to the first instruction of
	 * block bstBlock.
	 *
	 * @param srcBlock the source block
	 * @param dstBlock the destination block
	 */
	private void linkBlocks(BasicBlock srcBlock, BasicBlock dstBlock)
	{
		Instruction src = srcBlock.getExit();
		Instruction dst = dstBlock.getEntry();
		linkInstructions(src, dst);
	}

	/**
	 * Link the instructions of the given basic block.
	 *
	 * @param block
	 */
	private void linkInstructions(BasicBlock block)
	{
		Instruction src = null;
		for (Instruction dst : block.orderedInstructions())
		{
			if (src != null)
			{
				linkInstructions(src, dst);
			}
			src = dst;
		}
	}

	private void linkInstructions(Instruction src, Instruction dst)
	{
		if (src.isCall())
		{
			GraphOperations.addEdge(graph, src, dst, Traversals.INSTR_CFLOW_TRANSITIVE_EDGE);
		} else
		{
			GraphOperations.addEdge(graph, src, dst, Traversals.INSTR_CFLOW_EDGE);
		}
	}

}
