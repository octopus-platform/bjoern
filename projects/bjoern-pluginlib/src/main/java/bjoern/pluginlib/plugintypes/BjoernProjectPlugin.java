package bjoern.pluginlib.plugintypes;

import bjoern.pluginlib.connectors.BjoernProjectConnector;
import octopus.lib.plugintypes.OctopusProjectPlugin;

public abstract class BjoernProjectPlugin extends OctopusProjectPlugin
{
	public BjoernProjectPlugin()
	{
		setProjectConnector(new BjoernProjectConnector());
	}
}
