package exporters.outputModules.CSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import exporters.nodeStore.Node;

public class CSVWriter
{
	final static String SEPARATOR = "\t";

	final static String[] nodeProperties = { CSVFields.KEY, CSVFields.TYPE,
			CSVFields.ADDR, CSVFields.CHILD_NUM, CSVFields.FUNCTION_ID,
			CSVFields.REPR, CSVFields.CODE, CSVFields.COMMENT };

	final static String[] edgeProperties = {};
	final static String[] keyedEdgeProperties = {};

	static PrintWriter nodeWriter;
	static PrintWriter edgeWriter;
	static PrintWriter keyedEdgeWriter;

	static long lastNodeId = 0;

	static Map<Node, Long> objectToId = new HashMap<Node, Long>();

	public static void clear()
	{
		clearObjectCache();
	}

	private static void clearObjectCache()
	{
		for (Iterator<Map.Entry<Node, Long>> it = objectToId.entrySet()
				.iterator(); it.hasNext();)
		{
			Map.Entry<Node, Long> entry = it.next();
			if (!entry.getKey().isPermanent())
			{
				it.remove();
			}
		}
	}

	public static void finish()
	{
		closeEdgeFile();
		closeNodeFile();
		closeUnresolvedEdgeFile();
	}

	static public Long getIdForNode(Node o)
	{
		return objectToId.get(o);
	}

	public static void changeOutputDir(String dirNameForFileNode)
	{
		closeEdgeFile();
		closeNodeFile();
		closeUnresolvedEdgeFile();

		openNodeFile(dirNameForFileNode);
		openEdgeFile(dirNameForFileNode);
		openUnresolvedEdgeFile(dirNameForFileNode);
	}

	public static void addNode(Node node, Map<String, Object> properties)
	{
		// If the node has already been written, don't to anything
		Long id = objectToId.get(node);
		if (id != null)
			return;

		nodeWriter.write((new Long(lastNodeId)).toString());
		for (String property : nodeProperties)
		{
			nodeWriter.write(SEPARATOR);
			String propValue = (String) properties.get(property);
			if (propValue != null)
				nodeWriter.write(espaceAndQuote(propValue));
		}
		nodeWriter.write("\n");
		if (node != null)
			objectToId.put(node, lastNodeId);
		lastNodeId++;
	}

	private static String espaceAndQuote(String propValue)
	{
		return "\"" + propValue.replace("\"", "\\\"") + "\"";
	}

	public static void addEdge(long srcId, long dstId,
			Map<String, Object> properties, String edgeType)
	{
		edgeWriter.print(srcId);
		edgeWriter.print(SEPARATOR);
		edgeWriter.print(dstId);
		edgeWriter.print(SEPARATOR);
		edgeWriter.print(edgeType);

		for (String property : edgeProperties)
		{
			edgeWriter.write(SEPARATOR);
			String propValue = (String) properties.get(property);
			if (propValue != null)
				edgeWriter.write(propValue);
		}
		edgeWriter.write("\n");
	}

	public static void addUnresolvedEdge(String srcKey, String dstKey,
			Map<String, Object> properties, String edgeType)
	{
		keyedEdgeWriter.print(srcKey);
		keyedEdgeWriter.print(SEPARATOR);
		keyedEdgeWriter.print(dstKey);
		keyedEdgeWriter.print(SEPARATOR);
		keyedEdgeWriter.print(edgeType);
		// TODO: add properties
		keyedEdgeWriter.print("\n");
	}

	private static void openNodeFile(String outDir)
	{
		String path = outDir + File.separator + "nodes.csv";
		nodeWriter = createWriter(path);
		writeNodePropertyNames();
	}

	private static void writeNodePropertyNames()
	{
		String joined = CSVFields.ID + SEPARATOR
				+ StringUtils.join(nodeProperties, SEPARATOR);
		nodeWriter.println(joined);
	}

	private static void writeEdgePropertyNames()
	{
		String joined = "start" + SEPARATOR + "end" + SEPARATOR + "type"
				+ SEPARATOR + StringUtils.join(edgeProperties, SEPARATOR);
		edgeWriter.println(joined);
	}

	private static void openEdgeFile(String outDir)
	{
		String path = outDir + File.separator + "edges.csv";
		edgeWriter = createWriter(path);
		writeEdgePropertyNames();
	}

	private static void openUnresolvedEdgeFile(String outDir)
	{
		String path = outDir + File.separator + "keyedEdges.csv";
		keyedEdgeWriter = createWriter(path);
		writeKeyedEdgePropertyNames();
	}

	private static void writeKeyedEdgePropertyNames()
	{
		String joined = "nodeType_addr" + SEPARATOR + "nodeType_addr"
				+ SEPARATOR + "type" + SEPARATOR
				+ StringUtils.join(keyedEdgeProperties, SEPARATOR);
		keyedEdgeWriter.println(joined);
	}

	private static PrintWriter createWriter(String path)
	{
		try
		{
			return new PrintWriter(path);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException("Cannot create file: " + path);
		}
	}

	private static void closeNodeFile()
	{
		if (nodeWriter != null)
			nodeWriter.close();
	}

	private static void closeEdgeFile()
	{
		if (edgeWriter != null)
			edgeWriter.close();
	}

	private static void closeUnresolvedEdgeFile()
	{
		if (keyedEdgeWriter != null)
			keyedEdgeWriter.close();

	}

}
