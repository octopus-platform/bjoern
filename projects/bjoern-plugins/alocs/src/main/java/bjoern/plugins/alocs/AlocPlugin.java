package bjoern.plugins.alocs;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.OrientGraphConnectionPlugin;

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
		// TODO Auto-generated method stub

	}

}
