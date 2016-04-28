package bjoern.plugins.alocs;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.plugintypes.OrientGraphConnectionPlugin;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.r2interface.Radare;

public class AlocPlugin extends OrientGraphConnectionPlugin{

	Radare radare;

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();

		radare = new Radare();

		Iterable<Vertex> functions = LookupOperations.getAllFunctions(graph);
		createAlocsForFunctions(functions);

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

		BasicBlock entryBlock = Traversals.functionToEntryBlock(vertex);
		if(entryBlock == null){
			System.err.println("Warning: function without entry block");
			return;
		}

	}

}
