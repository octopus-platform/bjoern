package bjoern.plugins.instructionlinker;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.GraphOperations;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

public class InstructionLinkerPlugin extends OrientGraphConnectionPlugin
{
	private final static String[] CFLOW_EDGES = {
			EdgeTypes.CFLOW, EdgeTypes.CFLOW_TRUE, EdgeTypes.CFLOW_FALSE
	};

	private OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = orientConnector.getNoTxGraphInstance();

		Iterable<Vertex> iterable = LookupOperations.getAllBasicBlocks(graph);

		for (Vertex v : iterable)
		{
			BasicBlock block = new BasicBlock(v);
			linkInstructions(block);
			for (Vertex nextBasicBlock : block.getVertices(
					Direction.OUT, CFLOW_EDGES))
			{
				linkInstructions(block, new BasicBlock(nextBasicBlock));
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
	private void linkInstructions(BasicBlock srcBlock, BasicBlock dstBlock)
	{
		Instruction src = srcBlock.getExit();
		Instruction dst = dstBlock.getEntry();
		linkInstructions(src, dst);
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

	/**
	 * Link the instructions of the given basic block.
	 *
	 * @param block
	 */
	private void linkInstructions(BasicBlock block)
	{
		int size = block.getInstructions().size();
		if (size < 2)
		{
			return;
		}
		for (int i = 1; i < size; i++)
		{
			Instruction src = block.getInstructions().get(i - 1);
			Instruction dst = block.getInstructions().get(i);
			linkInstructions(src, dst);
		}
	}

}
