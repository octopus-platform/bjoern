package octopus.server.components.projectmanager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjectManager {

	private String projectsDir;

	public ProjectManager(String projectsDir)
	{
		this.projectsDir = projectsDir;
		openProjectsDir();
	}

	public String getPathToProject(String name)
	{
		return projectsDir + File.separator + name;
	}

	private void openProjectsDir()
	{
		if(Files.notExists(Paths.get(projectsDir))){
			new File(projectsDir).mkdirs();
		}
	}

	public void create(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		File dir = new File(getPathToProject(name));
		dir.mkdirs();
	}

	public void delete(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		File dir = new File(getPathToProject(name));
		dir.delete();
	}

}
