package server.components.orientdbImporter.processors;

import java.util.Iterator;

import server.Constants;
import server.components.orientdbImporter.CSVImporter;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable;

public class UnedgeProcessor extends EdgeProcessor
{

	public UnedgeProcessor(CSVImporter importer)
	{
		super(importer);
	}

	@Override
	protected Vertex lookupVertex(String id, Graph graph)
	{
		System.out.println(id);

		String fmt = "SELECT * FROM V WHERE %s LUCENE \"%s\"";
		String luceneQuery = "key:" + id;
		String queryStr = String.format(fmt, Constants.INDEX_NAME, luceneQuery);

		OCommandSQL query = new com.orientechnologies.orient.core.sql.OCommandSQL(
				queryStr);
		OrientDynaElementIterable result = importer.getNoTx().command(query)
				.execute();

		Iterator<Object> it = result.iterator();
		if (!it.hasNext())
			return null;

		return (Vertex) result.iterator().next();
	}
}
