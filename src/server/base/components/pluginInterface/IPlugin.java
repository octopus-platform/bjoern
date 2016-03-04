package server.base.components.pluginInterface;

import org.json.JSONObject;

public interface IPlugin
{
	void configure(JSONObject settings);

	void execute();
}