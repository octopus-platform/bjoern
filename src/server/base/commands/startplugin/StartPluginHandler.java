package server.base.commands.startplugin;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;
import org.json.JSONObject;
import server.base.components.pluginInterface.PluginRunnable;

public class StartPluginHandler extends OServerCommandAbstract
{

    String jarName;
    String dirName;
    JSONObject settings;

    public StartPluginHandler(final OServerCommandConfiguration iConfiguration)
    {
        readConfiguration(iConfiguration);
    }

    private void readConfiguration(OServerCommandConfiguration iConfiguration)
    {
        for (OServerEntryConfiguration param : iConfiguration.parameters)
        {
            switch (param.name)
            {
                case "dir":
                    dirName = param.value;
                    break;
            }
        }
    }

    @Override
    public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse)
            throws Exception
    {
        OLogManager.instance().warn(this, "startplugin");

        parseContent(iRequest.content);

        startPluginThread();

        iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, String.format(
                "plugin started: %s.\n", jarName), null);
        return false;

    }

    private void parseContent(String content)
    {
        JSONObject data = new JSONObject(content);
        jarName = data.getString("plugin");
        settings = data.getJSONObject("settings");
    }

    private void startPluginThread()
    {
        PluginRunnable runnable = new PluginRunnable();
        runnable.setJarName(jarName);
        runnable.setDirName(dirName);
        runnable.setSettings(settings);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public String[] getNames()
    {
        return new String[]{"POST|startplugin/"};
    }
}
