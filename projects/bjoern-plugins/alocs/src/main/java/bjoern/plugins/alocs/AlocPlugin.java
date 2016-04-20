package bjoern.plugins.alocs;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.OrientGraphConnectionPlugin;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.BasicBlock;

public class AlocPlugin extends OrientGraphConnectionPlugin{

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = getNoTxGraphInstance();
		Iterable<Vertex> functions = LookupOperations.getAllFunctions(graph);
		createAlocsForFunctions(functions);
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
		BasicBlock entryBlock = Traversals.functionToEntryBlock(vertex);
		if(entryBlock == null){
			System.err.println("Warning: function without entry block");
			return;
		}

	}

}
