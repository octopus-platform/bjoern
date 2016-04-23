package bjoern.plugins.radareimporter;

import java.io.IOException;

import org.json.JSONObject;

import bjoern.pluginlib.BjoernProject;
import bjoern.pluginlib.PluginAdapter;
import bjoern.r2interface.Radare;
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
		analysisBinaryWithR2(pathToBinary);

	}

	private void analysisBinaryWithR2(String pathToBinary)
	{
		String radareProjectFilename = project.getR2ProjectFilename();
		try {
			System.out.println(pathToBinary);
			Radare.loadBinary(pathToBinary);
			Radare.analyzeBinary();
			Radare.saveProject(radareProjectFilename);
		} catch (IOException e) {
			throw new RuntimeException("Error analyzing binary with r2");
		}

		// TODO: Export CSV files
		// TODO: Import CSV files into the graph database.
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
