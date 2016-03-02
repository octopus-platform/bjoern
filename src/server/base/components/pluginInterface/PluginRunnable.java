package server.base.components.pluginInterface;

public class PluginRunnable implements Runnable {

	String jarName;

	@Override
	public void run()
	{

		ClassLoader parentClassLoader = PluginClassLoader.class.getClassLoader();
		PluginClassLoader classLoader = new PluginClassLoader(parentClassLoader);
		classLoader.setJarFilename(jarName);
		Class myObjectClass = classLoader.loadClass("Plugin");

        try {
			IPlugin plugin = (IPlugin) myObjectClass.newInstance();
			plugin.execute();

        } catch (InstantiationException | IllegalAccessException e) {

			e.printStackTrace();
		}

	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

}
