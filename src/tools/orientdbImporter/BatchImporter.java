package tools.orientdbImporter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.opencsv.CSVReader;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class BatchImporter
{

	static CommandLineInterface cmdLine = new CommandLineInterface();
	static BatchGraph<OrientGraph> batchGraph;
	static String[] VertexKeys;
	static String[] EdgeKeys;
	private static OrientGraphNoTx noTx;

	public static void main(String[] args)
	{

		parseCommandLine(args);

		try
		{
			openDatabase();
			processNodeFile();
			processEdgeFile();
			closeDatabase();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void openDatabase()
	{
		OGlobalConfiguration.USE_WAL.setValue(false);
		OGlobalConfiguration.WAL_SYNC_ON_PAGE_FLUSH.setValue(false);

		OrientGraphFactory factory = new OrientGraphFactory(
				"plocal:/tmp/tempDB/", "admin", "admin");
		factory.declareIntent(new OIntentMassiveInsert());

		noTx = factory.getNoTx();
		noTx.declareIntent(new OIntentMassiveInsert());

		batchGraph = BatchGraph.wrap(noTx, 1000);

	}

	private static void closeDatabase()
	{
		batchGraph.shutdown();
		noTx.shutdown();
	}

	private static void parseCommandLine(String[] args)
	{
		try
		{
			cmdLine.parseCommandLine(args);
		}
		catch (RuntimeException | ParseException e)
		{
			printHelpAndTerminate(e);
		}
	}

	private static void processNodeFile() throws IOException
	{

		CSVReader csvReader = getCSVReaderForFile(cmdLine.getNodeFile());

		processFirstNodeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processNodeRow(row);
		}

	}

	private static void processFirstNodeRow(CSVReader csvReader)
			throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Node file is empty");

		initializeVertexKeys(row);
	}

	private static void initializeVertexKeys(String[] row)
	{
		VertexKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			VertexKeys[i] = row[i];
		}
	}

	private static void processNodeRow(String[] row)
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
		batchGraph.addVertex(id, properties);

	}

	private static void processEdgeFile() throws IOException
	{
		CSVReader csvReader = getCSVReaderForFile(cmdLine.getEdgeFile());
		processFirstEdgeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processEdgeRow(row);
		}

	}

	private static void processFirstEdgeRow(CSVReader csvReader)
			throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Edge file is empty");

		initializeEdgeKeys(row);
	}

	private static void initializeEdgeKeys(String[] row)
	{
		EdgeKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			EdgeKeys[i] = row[i];
		}
	}

	private static void processEdgeRow(String[] row)
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

	private static CSVReader getCSVReaderForFile(String filename)
			throws FileNotFoundException
	{
		CSVReader reader;
		FileReader fileReader = new FileReader(filename);
		reader = new CSVReader(fileReader, '\t');
		return reader;
	}

	private static void printHelpAndTerminate(Exception e)
	{
		System.err.println(e.getMessage());
		cmdLine.printHelp();
		System.exit(0);
	}

}
