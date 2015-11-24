package server.components.orientdbImporter;

public class ImportJob
{
	private final String nodeFilename;
	private final String edgeFilename;
	private final String unedgeFilename;
	private final String dbName;

	public ImportJob(String nodeFilename, String edgeFilename, String dbName,
			String unedgeFilename)
	{
		this.nodeFilename = nodeFilename;
		this.edgeFilename = edgeFilename;
		this.unedgeFilename = unedgeFilename;
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

	public String getUnedgeFilename()
	{
		return unedgeFilename;
	}

	public String getDbName()
	{
		return dbName;
	}

}