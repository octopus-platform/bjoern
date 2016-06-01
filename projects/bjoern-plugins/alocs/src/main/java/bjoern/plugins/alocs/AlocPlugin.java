package bjoern.plugins.alocs;

import java.io.IOException;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.plugintypes.RadareProjectPlugin;

public class AlocPlugin extends RadareProjectPlugin {

	OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = getOrientConnector().getNoTxGraphInstance();
		Iterable<Vertex> allFunctions = LookupOperations.getAllFunctions(graph);

		try{
			createAlocsForFunctions(allFunctions);
		} catch(RuntimeException exception){
			exception.printStackTrace();
		}

		graph.shutdown();
	}

	private void createAlocsForFunctions(Iterable<Vertex> functions) throws IOException
	{
		for(Vertex func : functions)
		{
			new FunctionAlocCreator(getRadare(), graph).createAlocsForFunction(func);
		}

	}

}
