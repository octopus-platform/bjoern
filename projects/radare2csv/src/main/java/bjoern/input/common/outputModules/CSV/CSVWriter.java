package bjoern.input.common.outputModules.CSV;

import bjoern.structures.Node;
import bjoern.structures.BjoernNodeProperties;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import orientdbimporter.CSVCommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class CSVWriter
{
	final static String SEPARATOR = "\t";

	final static String[] nodeProperties = {
			BjoernNodeProperties.KEY,
			BjoernNodeProperties.TYPE,
			BjoernNodeProperties.ADDR,
			BjoernNodeProperties.REPR,
			BjoernNodeProperties.CODE,
			BjoernNodeProperties.COMMENT,
			BjoernNodeProperties.ESIL
	};

	final static String[] edgeProperties = {};

	static PrintWriter nodeWriter;
	static PrintWriter edgeWriter;

	static Set<String> nodeLineSet = new HashSet<String>();
	static Set<String> edgeLineSet = new HashSet<String>();

	public static void finish()
	{
		writeNodeFile();
		writeEdgeFile();
		closeEdgeFile();
		closeNodeFile();
	}

	private static void writeNodeFile()
	{
		List<String> arr = new ArrayList<String>(nodeLineSet);
		Collections.sort(arr);
		Collections.reverse(arr);
		for (String csvLine : arr)
		{
			nodeWriter.write(csvLine);
		}
	}

	private static void writeEdgeFile()
	{
		List<String> arr = new ArrayList<String>(edgeLineSet);
		Collections.sort(arr);
		Collections.reverse(arr);
		for (String csvLine : arr)
		{
			edgeWriter.write(csvLine);
		}

	}


	public static void changeOutputDir(String dirNameForFileNode)
	{
		finish();

		openNodeFile(dirNameForFileNode);
		openEdgeFile(dirNameForFileNode);
	}

	public static void addNode(Node node)
	{
		String csvLine = CSVCommands.ADD;
		csvLine += generateNodePropertyString(node.getProperties());
		nodeLineSet.add(csvLine);
	}

	public static void addNoReplaceNode(Node node)
	{
		String csvLine = CSVCommands.ADD_NO_REPLACE;
		csvLine += generateNodePropertyString(node.getProperties());
		nodeLineSet.add(csvLine);
	}

	private static String generateNodePropertyString(Map<String, Object> properties)
	{
		StringBuilder sb = new StringBuilder();
		for (String property : nodeProperties)
		{
			sb.append(SEPARATOR);
			String propValue = (String) properties.get(property);
			if (propValue != null)
			{
				sb.append(escape(propValue));
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	private static String escape(String propValue)
	{
		return StringEscapeUtils.escapeCsv(propValue.replace("\\", "\\\\"));
	}


	public static void addEdge(String srcKey, String dstKey,
			Map<String, Object> properties, String edgeType)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(srcKey);
		sb.append(SEPARATOR);
		sb.append(dstKey);
		sb.append(SEPARATOR);
		sb.append(edgeType);
		// TODO: add properties
		sb.append("\n");

		edgeLineSet.add(sb.toString());
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
		String joined = "nodeType_addr"
				+ SEPARATOR
				+ "nodeType_addr"
				+ SEPARATOR
				+ "type"
				+ SEPARATOR
				+ StringUtils.join(edgeProperties, SEPARATOR);
		edgeWriter.println(joined);
	}

	private static PrintWriter createWriter(String path)
	{
		try
		{
			return new PrintWriter(path);
		} catch (FileNotFoundException e)
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

}
