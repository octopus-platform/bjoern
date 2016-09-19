package bjoern.plugins.usedefanalyzer;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

import java.util.List;

public class UseDefAnalyzerPlugin extends OrientGraphConnectionPlugin
{
	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		UseDefAnalyzer analyzer = new UseDefAnalyzer();
		for (Function function : LookupOperations.getFunctions(graph))
		{
			List<Aloc> alocs = Traversals.functionToAlocs(function);
			for (BasicBlock block : function.basicBlocks())
			{
				analyzer.analyze(block, alocs);
			}
		}
	}

}
