package bjoern.plugins.paramdetect;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.pluginlib.OrientGraphConnectionPlugin;

public class ParamDetectPlugin extends OrientGraphConnectionPlugin {

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = getNoTxGraphInstance();

	}
}

