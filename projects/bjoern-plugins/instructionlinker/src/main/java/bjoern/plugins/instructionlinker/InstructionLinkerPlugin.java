package bjoern.plugins.instructionlinker;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import octopus.lib.GraphOperations;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstructionLinkerPlugin extends OrientGraphConnectionPlugin
{
	public static final String RETURN = "RETURN";

	private OrientGraphNoTx graph;
	private static final Logger logger = LoggerFactory.getLogger(InstructionLinkerPlugin.class);

	@Override
	public void execute() throws Exception
	{
		graph = orientConnector.getNoTxGraphInstance();

		Iterable<Function> functions = LookupOperations.getFunctions(graph);

		int counter = 0;
		for (Function function : functions)
		{
			logger.info("Processing function " + ++counter);
			for (BasicBlock block : function.basicBlocks())
			{
				linkInstructions(block);
				for (BasicBlock nextBasicBlock : block.cflow())
				{
					linkBlocks(block, nextBasicBlock);
				}
			}
		}

		counter = 0;
		for (Function function : functions)
		{
			logger.info("Processing function " + ++counter);
			for (BasicBlock block : function.basicBlocks())
			{
				for (Instruction instruction : block.instructions())
				{
					if (instruction.isCall())
					{
						for (Instruction entry : instruction.call())
						{
							for (Instruction exit : entry.exits())
							{
								for (Instruction dst : new GremlinPipeline<>(instruction.getBaseVertex())
										.out(Traversals.INSTR_CFLOW_TRANSITIVE_EDGE)
										.transform(Instruction::new))
								{
									GraphOperations.addEdge(graph, exit, dst, RETURN);
								}
							}
						}
					}
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
		// Some basic blocks do not have instructions
		if (dst != null)
		{
			linkInstructions(src, dst);
		}
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
