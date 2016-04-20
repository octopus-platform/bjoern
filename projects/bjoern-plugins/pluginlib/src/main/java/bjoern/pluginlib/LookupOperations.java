package bjoern.pluginlib;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.nodeStore.NodeTypes;

public class LookupOperations {

	public static Iterable<Vertex> getAllBasicBlocks(OrientGraphNoTx graph)
	{
		Iterable<Vertex> iterable = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:" + NodeTypes.BASIC_BLOCK);
		return iterable;
	}

	public static Iterable<Vertex> getAllFunctions(OrientGraphNoTx graph)
	{
		Iterable<Vertex> functions = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:" + NodeTypes.FUNCTION);
		return functions;
	}

}
