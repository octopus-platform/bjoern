package octopus.server.components.projectmanager;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import org.apache.commons.io.FileUtils;
import orientdbimporter.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProjectManager
{

	private static Path projectsDir;
	private static Map<String, OctopusProject> nameToProject = new HashMap<String, OctopusProject>();

	public static void setProjectDir(Path newProjectsDir) throws IOException
	{
		if (!newProjectsDir.isAbsolute())
		{
			newProjectsDir = newProjectsDir.toAbsolutePath();
		}
		projectsDir = newProjectsDir.normalize();
		openProjectsDir();
		loadProjects();
	}

	private static void openProjectsDir() throws IOException
	{
		if (Files.notExists(projectsDir))
		{
			Files.createDirectories(projectsDir);
		}
	}

	private static void loadProjects() throws IOException
	{
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectsDir))
		{
			for (Path path : stream)
			{
				if (Files.isDirectory(path))
				{
					loadProject(path);
				}
			}
		}
	}

	private static void loadProject(Path projectDir)
	{
		String projectName = projectDir.getFileName().toString();
		OctopusProject newProject = createOctopusProjectForName(projectName);
		nameToProject.put(projectName, newProject);
	}

	public static boolean doesProjectExist(String name)
	{
		return nameToProject.containsKey(name);
	}

	public static OctopusProject getProjectByName(String name)
	{
		return nameToProject.get(name);
	}

	public static String getPathToProject(String name)
	{
		return Paths.get(projectsDir.toString(), name).toString();
	}

	public static void create(String name)
	{
		if (projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		if (doesProjectExist(name))
			throw new RuntimeException("Project already exists");

		OctopusProject project = createOctopusProjectForName(name);
		nameToProject.put(name, project);
	}

	public static void delete(String name)
	{
		if (projectsDir == null)
			throw new RuntimeException("Error: projectDir not set");

		deleteProjectWithName(name);
	}

	public static Iterable<String> listProjects()
	{
		return nameToProject.keySet();

	}

	private static OctopusProject createOctopusProjectForName(String name)
	{
		String pathToProject = getPathToProject(name);
		File dir = new File(pathToProject);
		dir.mkdirs();

		OctopusProject newProject = new OctopusProject();
		newProject.setPathToProjectDir(pathToProject);
		newProject.setDatabaseName(name);
		return newProject;
	}

	private static void deleteProjectWithName(String name)
	{
		File dir = new File(getPathToProject(name));
		try
		{
			FileUtils.deleteDirectory(dir);
			nameToProject.remove(name);
			removeDatabase(name);
		} catch (IOException e)
		{
			throw new RuntimeException("IO Exception on delete");
		}
	}

	private static void removeDatabase(String dbName) throws IOException
	{
		OServerAdmin admin;
		admin = new OServerAdmin("localhost/" + dbName).connect(
				Constants.DB_USERNAME, Constants.DB_PASSWORD);
		admin.dropDatabase("plocal");
	}

}
