package bjoern.plugins.datadependence;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

import java.util.Map;
import java.util.Set;

public class DataDependencePlugin extends OrientGraphConnectionPlugin
{
	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		ReachingDefinitionAnalyser analyser = new ReachingDefinitionAnalyser();
		for (Function function : LookupOperations.getFunctions(graph))
		{
			Instruction entry = Traversals
					.functionToEntryInstruction(function);
			if (null == entry)
			{
				continue;
			}
			Map<Vertex, Set<Edge>> reachingDefinitions = analyser
					.analyse(graph, entry);
			DataDependenceCreator
					.createFromReachingDefinitions(reachingDefinitions);

		}
	}
}
