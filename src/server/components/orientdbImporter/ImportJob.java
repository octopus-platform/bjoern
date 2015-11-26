package server.components.orientdbImporter;

public class ImportJob
{
	private final String nodeFilename;
	private final String edgeFilename;
	private final String keyedEdgeFilename;
	private final String dbName;

	public ImportJob(String nodeFilename, String edgeFilename, String dbName,
			String keyedEdgeFilename)
	{
		this.nodeFilename = nodeFilename;
		this.edgeFilename = edgeFilename;
		this.keyedEdgeFilename = keyedEdgeFilename;
		this.dbName = dbName;
	}

	public String getNodeFilename()
	{
		return nodeFilename;
	}

	public String getEdgeFilename()
	{
		return edgeFilename;
	}

	public String getKeyedEdgeFilename()
	{
		return keyedEdgeFilename;
	}

	public String getDbName()
	{
		return dbName;
	}

}