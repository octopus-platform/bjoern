package octopus.server.components.projectmanager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProjectManager {

	private static String projectsDir;
	private static Map<String, OctopusProject> nameToProject = new HashMap<String, OctopusProject>();

	public static void setProjectDir(String newProjectsDir)
	{
		projectsDir = newProjectsDir;
		openProjectsDir();
		loadProjects();
	}

	private static void openProjectsDir()
	{
		if(Files.notExists(Paths.get(projectsDir))){
			new File(projectsDir).mkdirs();
		}
	}


	private static void loadProjects()
	{
		File projectsDirHandle = new File(projectsDir);
		File[] files = projectsDirHandle.listFiles();
		for(File projectDir : files){
			if(!projectDir.isDirectory())
				continue;
			loadProject(projectDir);
		}
	}

	private static void loadProject(File projectDir)
	{
		OctopusProject newProject = new OctopusProject();

		String projectName = projectDir.getName();
		newProject.setPathToProjectDir(projectDir.getAbsolutePath());
		newProject.setDatabaseName(projectName);

		nameToProject.put(projectName, newProject);
	}

	public OctopusProject getProjectByName(String name)
	{
		return nameToProject.get(name);
	}

	public static String getPathToProject(String name)
	{
		return projectsDir + File.separator + name;
	}

	public static void create(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		File dir = new File(getPathToProject(name));
		dir.mkdirs();
	}

	public static void delete(String name)
	{
		if(projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		File dir = new File(getPathToProject(name));
		dir.delete();
	}

}
