package bjoern.pluginlib;

import octopus.server.components.pluginInterface.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginAdapter implements IPlugin
{

	private final Logger logger = LoggerFactory
			.getLogger(getClass());

	public Logger getLogger()
	{
		return this.logger;
	}
}
