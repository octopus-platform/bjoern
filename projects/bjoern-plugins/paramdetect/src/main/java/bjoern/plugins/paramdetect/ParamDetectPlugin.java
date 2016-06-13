package bjoern.plugins.paramdetect;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import octopus.lib.plugintypes.OrientGraphConnectionPlugin;

public class ParamDetectPlugin extends OrientGraphConnectionPlugin {

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		orientConnector.disconnect();
	}
}

