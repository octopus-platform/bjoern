package bjoern.plugins.radareimporter;

import org.json.JSONObject;

import bjoern.pluginlib.BjoernProject;
import bjoern.pluginlib.PluginAdapter;
import octopus.server.components.projectmanager.OctopusProject;
import octopus.server.components.projectmanager.ProjectManager;

public class RadareImporterPlugin extends PluginAdapter {

	String projectName;
	private BjoernProject project;

	@Override
	public void configure(JSONObject settings)
	{
		projectName = settings.getString("projectName");
	}

	@Override
	public void execute() throws Exception
	{
		project = openProject();
		String pathToBinary = project.getPathToBinary();
		analyzeBinaryWithR2(pathToBinary);

	}

	private void analyzeBinaryWithR2(String pathToBinary)
	{
		String radareProjectFilename = project.getR2ProjectFilename();

	}

	private BjoernProject openProject()
	{
		OctopusProject oProject = ProjectManager.getProjectByName(projectName);
		if(oProject == null)
			throw new RuntimeException("Error: project does not exist");

		return new BjoernProject(oProject);
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
