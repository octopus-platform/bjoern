package bjoern.input.common.outputModules.CSV;

import bjoern.structures.Node;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.DirectedEdge;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import orientdbimporter.CSVCommands;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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

	public static void finish()
	{
		closeEdgeFile();
		closeNodeFile();
	}

	public static void changeOutputDir(String dirNameForFileNode) throws IOException
	{
		finish();

		openNodeFile(dirNameForFileNode);
		openEdgeFile(dirNameForFileNode);
	}

	public static void writeNode(Node node)
	{
		String csvLine = CSVCommands.ADD;
		csvLine += generateNodePropertyString(node.getProperties());
		nodeWriter.println(csvLine);
	}

	public static void writeNoReplaceNode(Node node)
	{
		String csvLine = CSVCommands.ADD_NO_REPLACE;
		csvLine += generateNodePropertyString(node.getProperties());
		nodeWriter.println(csvLine);
	}

	public static void writeEdge(DirectedEdge edge)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(edge.getSourceKey());
		sb.append(SEPARATOR);
		sb.append(edge.getDestKey());
		sb.append(SEPARATOR);
		sb.append(edge.getType());
		// TODO: add properties
		edgeWriter.println(sb.toString());
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
		return sb.toString();
	}

	private static String escape(String propValue)
	{
		return StringEscapeUtils.escapeCsv(propValue.replace("\\", "\\\\"));
	}

	private static void openNodeFile(String outDir) throws IOException
	{
		Path path = Paths.get(outDir, "nodes.csv");
		nodeWriter = createWriter(path);
		nodeWriter.println(generateNodeFileHeader());
	}

	private static void openEdgeFile(String outDir) throws IOException
	{
		Path path = Paths.get(outDir, "edges.csv");
		edgeWriter = createWriter(path);
		edgeWriter.println(generateEdgeFileHeader());
	}

	private static String generateNodeFileHeader()
	{
		return "command"
				+ SEPARATOR
				+ StringUtils.join(nodeProperties, SEPARATOR);
	}

	private static String generateEdgeFileHeader()
	{
		return "nodeType_addr"
				+ SEPARATOR
				+ "nodeType_addr"
				+ SEPARATOR
				+ "type"
				+ SEPARATOR
				+ StringUtils.join(edgeProperties, SEPARATOR);
	}

	private static PrintWriter createWriter(Path path) throws IOException
	{
		return new PrintWriter(Files.newOutputStream(path));
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
