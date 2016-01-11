package exporters.outputModules.CSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

	static PrintWriter nodeWriter;
	static PrintWriter edgeWriter;

	public static void finish()
	{
		closeEdgeFile();
		closeNodeFile();
	}

	public static void changeOutputDir(String dirNameForFileNode)
	{
		finish();

		openNodeFile(dirNameForFileNode);
		openEdgeFile(dirNameForFileNode);
	}

	public static void addNode(Node node, Map<String, Object> properties)
	{
		nodeWriter.write(CSVCommands.ADD);
		writeNodeProperties(properties);
	}

	private static void writeNodeProperties(Map<String, Object> properties) {
		for (String property : nodeProperties)
		{
			nodeWriter.write(SEPARATOR);
			String propValue = (String) properties.get(property);
			if (propValue != null)
				nodeWriter.write(espaceAndQuote(propValue));
		}
		nodeWriter.write("\n");
	}

	private static String espaceAndQuote(String propValue)
	{
		return "\"" + propValue.replace("\"", "\\\"") + "\"";
	}


	public static void addEdge(String srcKey, String dstKey,
			Map<String, Object> properties, String edgeType)
	{
		edgeWriter.print(srcKey);
		edgeWriter.print(SEPARATOR);
		edgeWriter.print(dstKey);
		edgeWriter.print(SEPARATOR);
		edgeWriter.print(edgeType);
		// TODO: add properties
		edgeWriter.print("\n");
	}

	private static void openNodeFile(String outDir)
	{
		String path = outDir + File.separator + "nodes.csv";
		nodeWriter = createWriter(path);
		nodeWriter.write("command" + SEPARATOR);
		writeNodePropertyNames();
	}

	private static void writeNodePropertyNames()
	{
		String joined = StringUtils.join(nodeProperties, SEPARATOR);
		nodeWriter.println(joined);
	}

	private static void openEdgeFile(String outDir)
	{
		String path = outDir + File.separator + "edges.csv";
		edgeWriter = createWriter(path);
		writeEdgePropertyNames();
	}

	private static void writeEdgePropertyNames()
	{
		String joined = "nodeType_addr" + SEPARATOR + "nodeType_addr"
				+ SEPARATOR + "type" + SEPARATOR
				+ StringUtils.join(edgeProperties, SEPARATOR);
		edgeWriter.println(joined);
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

	public static void addNoReplaceNode(Node node, Map<String, Object> properties)
	{
		nodeWriter.write(CSVCommands.ADD_NO_REPLACE);
		writeNodeProperties(properties);
	}

}
