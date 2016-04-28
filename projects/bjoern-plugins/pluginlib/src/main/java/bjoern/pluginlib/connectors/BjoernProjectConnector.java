package bjoern.pluginlib.connectors;

import bjoern.pluginlib.BjoernProject;
import octopus.server.components.projectmanager.OctopusProject;
import octopus.server.components.projectmanager.ProjectManager;

public class BjoernProjectConnector {

	BjoernProject project;

	public void connect(String projectName)
	{
		project = openProject(projectName);
	}

	protected BjoernProject openProject(String projectName)
	{
		OctopusProject oProject = ProjectManager.getProjectByName(projectName);
		if(oProject == null)
			throw new RuntimeException("Error: project does not exist");

		return new BjoernProject(oProject);
	}

	public void disconnect()
	{
		// TODO Auto-generated method stub
	}

	public BjoernProject getProject()
	{
		return project;
	}

}
