package bjoern.pluginlib.connectors;

import bjoern.pluginlib.BjoernProject;
import octopus.lib.OctopusProjectWrapper;
import octopus.lib.connectors.OctopusProjectConnector;
import octopus.server.components.projectmanager.OctopusProject;

public class BjoernProjectConnector extends OctopusProjectConnector {

	@Override
	protected OctopusProjectWrapper createNewProject(OctopusProject oProject)
	{
		BjoernProject bjoernProject = new BjoernProject();
		bjoernProject.setWrappedProject(oProject);
		return bjoernProject;
	}
}
