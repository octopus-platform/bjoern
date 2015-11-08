package tools.server;

import java.io.File;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		OServer server = OServerMain.create();
		server.startup(new File("conf/orientdb-server-config.xml"));
		server.activate();
	}
}
