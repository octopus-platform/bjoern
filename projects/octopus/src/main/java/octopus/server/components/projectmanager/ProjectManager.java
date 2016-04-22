package octopus.server.components.projectmanager;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjectManager {

	private String projectsDir;

	public ProjectManager(String projectsDir)
	{
		this.projectsDir = projectsDir;
		ensureProjectDirExists();
	}

	private void ensureProjectDirExists()
	{
		if(Files.notExists(Paths.get(projectsDir)))
			throw new RuntimeException("Error: projectDir does not exist");
	}

	public void create(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		System.out.println("create");
	}

	public void delete(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		System.out.println("delete");
	}

}
