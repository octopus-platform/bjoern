package server.base.components.pluginInterface;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.bjoern.commands.dumpcfg.OServerCommandGetDumpCFG;

import java.nio.file.Paths;

public class PluginRunnable implements Runnable
{

    String jarName;
    String dirName;

    private static final Logger logger = LoggerFactory
            .getLogger(OServerCommandGetDumpCFG.class);
    private JSONObject settings;

    @Override
    public void run()
    {
        try
        {
            IPlugin plugin = newInstance();
            if (plugin != null)
            {
                plugin.configure(settings);
                plugin.execute();
            }
        } catch (InstantiationException | IllegalAccessException e)
        {
            logger.error(e.getMessage());
        }
    }

    public void setJarName(String jarName)
    {
        this.jarName = jarName;
    }

    public void setDirName(String dirName)
    {
        this.dirName = dirName;
    }

    public void setSettings(JSONObject settings)
    {
        this.settings = settings;
    }

    private IPlugin newInstance()
            throws InstantiationException, IllegalAccessException
    {
        ClassLoader parentClassLoader = PluginClassLoader.class
                .getClassLoader();
        PluginClassLoader classLoader = new PluginClassLoader(
                parentClassLoader);
        classLoader.setJarFilename(Paths.get(dirName, jarName).toString());
        Class<?> myObjectClass = classLoader.loadClass("Plugin");
        try
        {
            return (IPlugin) myObjectClass.newInstance();
        } catch (ClassCastException e)
        {
            logger.error("Invalid plugin");
        } catch (NullPointerException e)
        {
            logger.error("Plugin not found: " + jarName);
        }
        return null;
    }

}
