package bjoern.plugins.alocs;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.plugintypes.RadareProjectPlugin;
import bjoern.pluginlib.structures.BasicBlock;

public class AlocPlugin extends RadareProjectPlugin {


	@Override
	public void execute() throws Exception
	{
		System.out.println("reached");
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		Iterable<Vertex> allFunctions = LookupOperations.getAllFunctions(graph);
		createAlocsForFunctions(allFunctions);
		graph.shutdown();
	}

	private void createAlocsForFunctions(Iterable<Vertex> functions)
	{
		for(Vertex func : functions)
		{
			createAlocsForFunction(func);
		}

	}

	private void createAlocsForFunction(Vertex vertex)
	{
		// Determine all registers read by the function
		// Determine all registers written by the function

		// For each of these registers, create a database node
		// Connect functions with READS/WRITES edges to these
		// database nodes.

		// Next, determine registers read/written for each basic block

		String addr = vertex.getProperty("addr");
		System.out.println(addr);

		// radare.getRegistersUsedByFunc(addr)


		BasicBlock entryBlock = Traversals.functionToEntryBlock(vertex);
		if(entryBlock == null){
			System.err.println("Warning: function without entry block");
			return;
		}

	}

}
