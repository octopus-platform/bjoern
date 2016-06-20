package bjoern.pluginlib;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.structures.Function;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LookupOperations
{

	@Deprecated
	public static Iterable<Vertex> getAllFunctions(OrientGraphNoTx graph)
	{
		Iterable<Vertex> functions = graph.command(
				BjoernConstants.LUCENE_QUERY).execute("nodeType:" + NodeTypes.FUNCTION);
		return functions;
	}

	public static Iterable<Function> getFunctions(OrientGraphNoTx graph)
	{
		boolean parallel = true;
		Iterable<Vertex> functions = graph.command(BjoernConstants.LUCENE_QUERY)
				.execute("nodeType:" + NodeTypes.FUNCTION);
		return StreamSupport.stream(functions.spliterator(), parallel).map(Function::new)
				.collect(Collectors.toList());
	}
}
