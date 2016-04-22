package bjoern.pluginlib;

import octopus.server.components.projectmanager.OctopusProject;

public class BjoernProject {

	private OctopusProject oProject;

	public BjoernProject(OctopusProject octopusProject)
	{
		oProject = octopusProject;
	}

	public String getPathToProjectDir()
	{
		return oProject.getPathToProjectDir();
	}

	public String getDatabaseName()
	{
		return oProject.getDatabaseName();
	}

}
