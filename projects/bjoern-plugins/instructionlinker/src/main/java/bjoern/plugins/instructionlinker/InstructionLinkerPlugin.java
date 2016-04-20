package bjoern.plugins.instructionlinker;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.GraphOperations;
import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.OrientGraphConnectionPlugin;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.structures.edges.EdgeTypes;

public class InstructionLinkerPlugin extends OrientGraphConnectionPlugin
{
	private final static String[] CFLOW_EDGES = {
			EdgeTypes.CFLOW, EdgeTypes.CFLOW_TRUE, EdgeTypes.CFLOW_FALSE
	};

	private OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = getNoTxGraphInstance();

		Iterable<Vertex> iterable = LookupOperations.getAllBasicBlocks(graph);

		for (Vertex v : iterable)
		{
			BasicBlock block = new BasicBlock(v);
			linkInstructions(block);
			for (Vertex nextBasicBlock : block.getNode().getVertices(
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
		GraphOperations.addEdge(graph, src, dst);
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
			GraphOperations.addEdge(graph, src, dst);
		}
	}

}
