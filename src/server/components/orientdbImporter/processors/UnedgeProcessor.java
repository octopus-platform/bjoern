package server.components.orientdbImporter.processors;

import server.Constants;
import server.components.orientdbImporter.CSVImporter;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class UnedgeProcessor extends EdgeProcessor
{

	public UnedgeProcessor(CSVImporter importer)
	{
		super(importer);
	}

	@Override
	protected Vertex lookupVertex(String id, Graph graph)
	{

		String fmt = "SELECT * FROM V WHERE %s LUCENE \"%s\"";
		String luceneQuery = "n";
		String queryStr = String.format(fmt, Constants.INDEX_NAME, luceneQuery);
		System.out.println(queryStr);
		OCommandSQL query = new com.orientechnologies.orient.core.sql.OCommandSQL(
				queryStr);
		Object result = importer.getNoTx().command(query).execute();
		System.out.println(result);

		return null;
	}
}
