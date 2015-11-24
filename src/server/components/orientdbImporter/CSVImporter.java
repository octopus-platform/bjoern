package server.components.orientdbImporter;

import java.io.IOException;

import server.Constants;
import server.components.orientdbImporter.processors.EdgeProcessor;
import server.components.orientdbImporter.processors.NodeProcessor;
import server.components.orientdbImporter.processors.UnedgeProcessor;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class CSVImporter
{
	private BatchGraph<?> batchGraph;
	private String[] VertexKeys;
	private String[] EdgeKeys;
	private boolean isNewDatabase;
	private OrientGraphNoTx noTx;
	private String dbName;

	public void importCSVFiles(String nodeFile, String edgeFile,
			String unedgeFile) throws IOException
	{
		openDatabase();
		processNodeFile(nodeFile);
		processEdgeFile(edgeFile);
		processUnedgeFile(unedgeFile);
		closeDatabase();
	}

	private void openDatabase() throws IOException
	{
		OGlobalConfiguration.USE_WAL.setValue(false);
		OGlobalConfiguration.WAL_SYNC_ON_PAGE_FLUSH.setValue(false);

		isNewDatabase = !databaseExists(dbName);
		noTx = new OrientGraphNoTx(Constants.PLOCAL_REL_PATH_TO_DBS + dbName);
		getNoTx().declareIntent(new OIntentMassiveInsert());

		batchGraph = BatchGraph.wrap(getNoTx(), 1000);
	}

	private void processNodeFile(String filename) throws IOException
	{
		(new NodeProcessor(this)).process(filename);
	}

	private void processEdgeFile(String filename) throws IOException
	{
		(new EdgeProcessor(this)).process(filename);
	}

	private void processUnedgeFile(String filename) throws IOException
	{
		(new UnedgeProcessor(this)).process(filename);
	}

	private boolean databaseExists(String dbName) throws IOException
	{
		return new OServerAdmin("localhost/" + dbName).connect(
				Constants.DB_USERNAME, Constants.DB_PASSWORD).existsDatabase();
	}

	private void closeDatabase()
	{
		batchGraph.shutdown();
		getNoTx().shutdown();
	}

	// Getters and setters...

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	public boolean isNewDatabase()
	{
		return isNewDatabase;
	}

	public String[] getVertexKeys()
	{
		return VertexKeys;
	}

	public void setVertexKeys(String[] vertexKeys)
	{
		VertexKeys = vertexKeys;
	}

	public String[] getEdgeKeys()
	{
		return EdgeKeys;
	}

	public void setEdgeKeys(String[] edgeKeys)
	{
		EdgeKeys = edgeKeys;
	}

	public OrientGraphNoTx getNoTx()
	{
		return noTx;
	}

	public BatchGraph<?> getBatchGraph()
	{
		return batchGraph;
	}

}
