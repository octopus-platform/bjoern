package bjoern.plugins.datadependence;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataDependencePlugin extends OrientGraphConnectionPlugin {
	@Override
	public void execute() throws Exception {
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		ReachingDefinitionAnalyser analyser = new ReachingDefinitionAnalyser(
				DefinitionProvider::generatedDefinitions,
				DefinitionProvider::killedDefinitions);
		for (Function function : LookupOperations.getFunctions(graph)) {
			Instruction entry = Traversals
					.functionToEntryInstruction(function);
			if (null == entry) {
				continue;
			}
			Map<Vertex, Set<ReachingDefinitionAnalyser.Definition>>
					reachingDefinitions = analyser
					.analyse(entry);
			DataDependenceCreator
					.createFromReachingDefinitions(reachingDefinitions);

		}
	}

	private static class DefinitionProvider {

		public static Set<ReachingDefinitionAnalyser.Definition> generatedDefinitions(
				final Vertex vertex) {
			Set<ReachingDefinitionAnalyser.Definition> genSet = new HashSet<>();
			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
			pipe.start(vertex)
			    .out("WRITE")
			    .out("BELONGS_TO")
			    .in("BELONGS_TO");
			for (Vertex register : pipe) {
				String registerName = register.getProperty("name");
				genSet.add(new ReachingDefinitionAnalyser.Definition(vertex,
						registerName));
			}
			return genSet;
		}

		public static Set<ReachingDefinitionAnalyser.Definition> killedDefinitions(
				final Vertex vertex) {
			Set<ReachingDefinitionAnalyser.Definition> killSet = new HashSet<>();
			GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<>();
			pipe.start(vertex)
			    .out("WRITE")
			    .out("BELONGS_TO")
			    .in("BELONGS_TO")
			    .inE("WRITE");
			for (Edge writeEdge : pipe) {
				Vertex genVertex = writeEdge.getVertex(Direction.OUT);
				Vertex aloc = writeEdge.getVertex(Direction.IN);
				GremlinPipeline<Vertex, Object> pipe2 = new
						GremlinPipeline<>();
				pipe2.start(aloc)
				     .out("BELONGS_TO")
				     .in("BELONGS_TO")
				     .property("name");
				for (Object identifier : pipe2) {
					killSet.add(new ReachingDefinitionAnalyser.Definition(
							genVertex, identifier));
				}
			}
			return killSet;
		}
	}
}
