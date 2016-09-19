package bjoern.plugins.vsa;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.structures.Function;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VSAPlugin extends OrientGraphConnectionPlugin
{
	private Logger logger = LoggerFactory.getLogger(VSAPlugin.class);

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		VSA vsa = new VSA();
		for (Function function : LookupOperations.getFunctions(graph))
		{
			try
			{
				logger.info(function.toString());
				vsa.performIntraProceduralVSA(function);
			} catch (Exception e)
			{
				logger.error("Error for function " + function + ": " + e.getMessage());
			}
		}
		graph.shutdown();
	}

}
