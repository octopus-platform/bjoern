package server.commands.importcsv;

public class GraphFiles
{
	public GraphFiles(String nodeFilename, String edgeFilename)
	{
		this.nodeFilename = nodeFilename;
		this.edgeFilename = edgeFilename;
	}

	public String getNodeFilename()
	{
		return nodeFilename;
	}

	public String getEdgeFilename()
	{
		return edgeFilename;
	}

	private final String nodeFilename;
	private final String edgeFilename;
}