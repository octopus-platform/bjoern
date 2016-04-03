package octopus.server.components.pluginInterface;

import org.json.JSONObject;

public interface IPlugin
{
    void configure(JSONObject settings);

    void execute() throws Exception;

    void beforeExecution() throws Exception;

    void afterExecution() throws Exception;
}