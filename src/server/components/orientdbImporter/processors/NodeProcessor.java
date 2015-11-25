package server.components.orientdbImporter.processors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import server.components.orientdbImporter.CSVImporter;

import com.opencsv.CSVReader;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

import exporters.outputModules.CSV.CSVFields;

public class NodeProcessor extends CSVFileProcessor
{

	public NodeProcessor(CSVImporter importer)
	{
		super(importer);
	}

	@Override
	protected void processFirstRow(CSVReader csvReader, String[] row)
			throws IOException
	{

		initializeVertexKeys(row);
		createPropertiesAndIndices();

	}

	private void initializeVertexKeys(String[] row)
	{
		String[] keys = rowToKeys(row);
		importer.setVertexKeys(keys);
	}

	private void createPropertiesAndIndices()
	{

		if (!importer.isNewDatabase())
			return;

		OrientVertexType vType = importer.getNoTx().getVertexType("V");

		for (String key : importer.getVertexKeys())
		{
			vType.createProperty(key, OType.STRING);
		}

		List<String> keysToIndex = new LinkedList<String>();
		for (String key : importer.getVertexKeys())
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

	@Override
	protected void processRow(String[] row)
	{
		// skip empty lines
		if (row.length < 1)
			return;

		String id = row[0];

		String[] properties = new String[2 * (row.length - 1)];
		for (int i = 1; i < row.length; i++)
		{
			properties[2 * (i - 1)] = importer.getVertexKeys()[i];
			properties[2 * (i - 1) + 1] = row[i];
		}
		Object[] props = properties;
		BatchGraph<?> batchGraph = (BatchGraph<?>) importer.getGraph();
		batchGraph.addVertex(id, props);
	}
}
