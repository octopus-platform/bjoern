package bjoern.pluginlib;

import java.io.File;

import octopus.lib.OctopusProjectWrapper;

public class BjoernProject extends OctopusProjectWrapper {

	public String getPathToBinary()
	{
		return getPathToProjectDir() + File.separator + "binary";
	}

	public String getR2ProjectFilename()
	{
		return getPathToProjectDir() + File.separator + "radareProject";
	}

	public String getNodeFilename()
	{
		return getPathToProjectDir() + File.separator + "nodes.csv";
	}

	public String getEdgeFilename()
	{
		return getPathToProjectDir() + File.separator + "edges.csv";
	}

}
