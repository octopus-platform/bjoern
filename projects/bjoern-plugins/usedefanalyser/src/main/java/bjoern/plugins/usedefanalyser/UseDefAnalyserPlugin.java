package bjoern.plugins.usedefanalyser;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UseDefAnalyserPlugin extends OrientGraphConnectionPlugin {
	private Logger logger = LoggerFactory.getLogger(UseDefAnalyser.class);

	@Override
	public void execute() throws Exception {
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		UseDefAnalyser analyzer = new UseDefAnalyser();
		for (Function function : LookupOperations.getFunctions(graph)) {
			logger.info(function.toString());
			for (BasicBlock block : function.basicBlocks()) {
				try {
					analyzer.analyse(block);
				} catch (Exception e) {
					logger.error("Error while analysing block @0x"
							+ Long.toHexString(block.getAddress())
							+ " of function " + function + ": "
							+ e.getMessage());
				}
			}
		}
	}

}
