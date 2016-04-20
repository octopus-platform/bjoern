package bjoern.pluginlib;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.nodeStore.NodeTypes;

public class LookupOperations {

	public static Iterable<Vertex> getAllBasicBlocks(OrientGraphNoTx graph)
	{
		String luceneQuery = "nodeType:" + NodeTypes.BASIC_BLOCK;
		OCommandRequest cmd = graph.command(BjoernConstants.LUCENE_QUERY);
		Iterable<Vertex> iterable = cmd.execute(luceneQuery);

		return iterable;
	}

	public static Iterable<Vertex> getAllFunctions(OrientGraphNoTx graph)
	{
		Iterable<Vertex> functions = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:" + NodeTypes.FUNCTION);
		return functions;
	}

}
