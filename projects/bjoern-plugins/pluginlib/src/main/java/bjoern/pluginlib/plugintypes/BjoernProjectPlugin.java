package bjoern.pluginlib.plugintypes;

import java.io.IOException;

import org.json.JSONObject;

import com.orientechnologies.orient.client.remote.OServerAdmin;

import bjoern.pluginlib.connectors.BjoernProjectConnector;
import octopus.server.components.pluginInterface.Plugin;
import orientdbimporter.Constants;

public class BjoernProjectPlugin extends Plugin {

	protected BjoernProjectConnector bjoernProjectConnector = new BjoernProjectConnector();

	@Override
	public void configure(JSONObject settings)
	{
		String projectName = settings.getString("projectName");
		bjoernProjectConnector.connect(projectName);
	}

	protected void raiseIfDatabaseForProjectExists()
	{
		String dbName = bjoernProjectConnector.getProject().getDatabaseName();

		boolean databaseExists = doesDatabaseExist(dbName);
		if(databaseExists)
			throw new RuntimeException("Database already exists. Skipping.");
	}

	private boolean doesDatabaseExist(String dbName)
	{
		try {
			return new OServerAdmin("localhost/" + dbName).connect(
					Constants.DB_USERNAME, Constants.DB_PASSWORD).existsDatabase();

		} catch (IOException e) {
			throw new RuntimeException("Error determining whether database exists");
		}
	}


}
