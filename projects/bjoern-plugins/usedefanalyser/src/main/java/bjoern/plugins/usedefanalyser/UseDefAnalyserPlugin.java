package bjoern.plugins.usedefanalyser;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

public class UseDefAnalyserPlugin extends OrientGraphConnectionPlugin {
	@Override
	public void execute() throws Exception {
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		UseDefAnalyser analyzer = new UseDefAnalyser();
		for (Function function : LookupOperations.getFunctions(graph)) {
			for (BasicBlock block : function.basicBlocks()) {
				analyzer.analyse(block);
			}
		}
	}

}
