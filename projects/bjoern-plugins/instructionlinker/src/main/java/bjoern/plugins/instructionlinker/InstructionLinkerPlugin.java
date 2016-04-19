package bjoern.plugins.instructionlinker;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.BjoernConstants;
import bjoern.pluginlib.OrientGraphConnectionPlugin;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;

public class InstructionLinkerPlugin extends OrientGraphConnectionPlugin
{
	private final static String[] CFLOW_EDGES = {
			"CFLOW_ALWAYS", "CFLOW_TRUE", "CFLOW_FALSE"
	};
	private final static String INSTR_CFLOW_EDGE = "NEXT_INSTR";

	private OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = getNoTxGraphInstance();
		Iterable<Vertex> iterable = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:BB");

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
		addEdge(src, dst);
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
			addEdge(src, dst);
		}
	}

	/**
	 * Add an edge from the instruction src to the instruction dst if it does
	 * not already exist.
	 * <p>
	 *
	 * @param src the source of the edge
	 * @param dst the destination of the edge
	 */
	private void addEdge(Instruction src, Instruction dst)
	{
		for (Edge edge : src.getNode().getEdges(Direction.OUT,
				INSTR_CFLOW_EDGE))
		{
			if (edge.getVertex(Direction.IN).equals(dst.getNode()))
			{
				return;
			}
		}
			graph.addEdge(0, src.getNode(), dst.getNode(), INSTR_CFLOW_EDGE);
	}
}
