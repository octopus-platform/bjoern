package radareimporter;

import org.json.JSONObject;

import bjoern.pluginlib.PluginAdapter;
import octopus.server.components.projectmanager.OctopusProject;
import octopus.server.components.projectmanager.ProjectManager;

public class RadareImporterPlugin extends PluginAdapter {

	String projectName;

	@Override
	public void configure(JSONObject settings)
	{
		projectName = settings.getString("projectName");
	}

	@Override
	public void execute() throws Exception
	{
		OctopusProject project = openProject();

	}

	private OctopusProject openProject()
	{
		OctopusProject project = ProjectManager.getProjectByName(projectName);
		if(project == null)
			throw new RuntimeException("Error: project does not exist");

		return project;
	}

	@Override
	public void beforeExecution() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterExecution() throws Exception {
		// TODO Auto-generated method stub

	}

}
