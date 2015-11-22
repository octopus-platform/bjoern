package server.commands.importcsv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import server.commands.Constants;

import com.opencsv.CSVReader;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

import exporters.outputModules.CSV.CSVFields;

public class CSVImporter
{
	BatchGraph<?> batchGraph;
	String[] VertexKeys;
	String[] EdgeKeys;
	private boolean isNewDatabase;
	private OrientGraphNoTx noTx;

	public void importCSVFiles(String nodeFile, String edgeFile)
			throws IOException
	{
		openDatabase();
		processNodeFile(nodeFile);
		processEdgeFile(edgeFile);
		closeDatabase();
	}

	private void openDatabase() throws IOException
	{
		OGlobalConfiguration.USE_WAL.setValue(false);
		OGlobalConfiguration.WAL_SYNC_ON_PAGE_FLUSH.setValue(false);

		isNewDatabase = !databaseExists(Constants.DB_NAME);

		noTx = new OrientGraphNoTx(Constants.PLOCAL_PATH_TO_DB);
		noTx.declareIntent(new OIntentMassiveInsert());

		batchGraph = BatchGraph.wrap(noTx, 1000);
	}

	private boolean databaseExists(String dbName) throws IOException
	{
		return new OServerAdmin("localhost/" + Constants.DB_NAME).connect(
				Constants.DB_USERNAME, Constants.DB_PASSWORD).existsDatabase();
	}

	private void processNodeFile(String filename) throws IOException
	{

		CSVReader csvReader = getCSVReaderForFile(filename);

		processFirstNodeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processNodeRow(row);
		}

	}

	private void processFirstNodeRow(CSVReader csvReader) throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Node file is empty");

		initializeVertexKeys(row);
		createPropertiesAndIndices();
	}

	private void initializeVertexKeys(String[] row)
	{
		VertexKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			VertexKeys[i] = row[i];
		}
	}

	private void createPropertiesAndIndices()
	{
		if (!isNewDatabase)
			return;

		OrientVertexType vType = noTx.getVertexType("V");

		for (String key : VertexKeys)
		{
			vType.createProperty(key, OType.STRING);
		}

		List<String> keysToIndex = new LinkedList<String>();
		for (String key : VertexKeys)
		{
			if (key.equals(CSVFields.ID))
				continue;
			keysToIndex.add(key);
		}

		String[] indexKeys = new String[keysToIndex.size()];
		keysToIndex.sort(null);
		keysToIndex.toArray(indexKeys);

		vType.createIndex("nodeIndex.", "FULLTEXT", null, null, "LUCENE",
				indexKeys);
	}

	private void processNodeRow(String[] row)
	{

		// skip empty lines
		if (row.length < 1)
			return;

		String id = row[0];

		String[] properties = new String[2 * (row.length - 1)];
		for (int i = 1; i < row.length; i++)
		{
			properties[2 * (i - 1)] = VertexKeys[i];
			properties[2 * (i - 1) + 1] = row[i];
		}
		Object[] props = properties;
		batchGraph.addVertex(id, props);

	}

	private void processEdgeFile(String filename) throws IOException
	{
		CSVReader csvReader = getCSVReaderForFile(filename);
		processFirstEdgeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processEdgeRow(row);
		}

	}

	private void processFirstEdgeRow(CSVReader csvReader) throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Edge file is empty");

		initializeEdgeKeys(row);
	}

	private void initializeEdgeKeys(String[] row)
	{
		EdgeKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			EdgeKeys[i] = row[i];
		}
	}

	private void processEdgeRow(String[] row)
	{

		if (row.length < 3)
			return;

		String srcId = row[0];
		String dstId = row[1];
		String label = row[2];

		Vertex outVertex = batchGraph.getVertex(srcId);
		Vertex inVertex = batchGraph.getVertex(dstId);

		Edge edge = batchGraph.addEdge(0, outVertex, inVertex, label);

		for (int i = 3; i < row.length; i++)
		{
			edge.setProperty(EdgeKeys[i], row[i]);
		}

	}

	private CSVReader getCSVReaderForFile(String filename)
			throws FileNotFoundException
	{
		CSVReader reader;
		FileReader fileReader = new FileReader(filename);
		reader = new CSVReader(fileReader, '\t');
		return reader;
	}

	private void closeDatabase()
	{
		batchGraph.shutdown();
		noTx.shutdown();
	}

}
