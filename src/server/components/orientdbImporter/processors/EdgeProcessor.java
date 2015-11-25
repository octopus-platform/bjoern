package server.components.orientdbImporter.processors;

import java.io.IOException;

import server.components.orientdbImporter.CSVImporter;

import com.opencsv.CSVReader;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class EdgeProcessor extends CSVFileProcessor
{

	public EdgeProcessor(CSVImporter importer)
	{
		super(importer);
	}

	@Override
	protected void processFirstRow(CSVReader csvReader, String[] row)
			throws IOException
	{
		initializeEdgeKeys(row);
	}

	private void initializeEdgeKeys(String[] row)
	{
		String[] keys = rowToKeys(row);
		importer.setEdgeKeys(keys);
	}

	@Override
	protected void processRow(String[] row)
	{
		if (row.length < 3)
			return;

		String srcId = row[0];
		String dstId = row[1];
		String label = row[2];

		Graph graph = importer.getGraph();

		Vertex outVertex = lookupVertex(srcId, graph);
		Vertex inVertex = lookupVertex(dstId, graph);

		Edge edge = graph.addEdge(0, outVertex, inVertex, label);

		for (int i = 3; i < row.length; i++)
		{
			edge.setProperty(importer.getEdgeKeys()[i], row[i]);
		}
	}

	protected Vertex lookupVertex(String id, Graph batchGraph)
	{
		return batchGraph.getVertex(id);
	}

}
