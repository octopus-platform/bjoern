package server.components.cfgdump;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.io.gml.GMLWriter;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

public class CFGDumpRunnable implements Runnable
{
	private static final Logger logger = LoggerFactory
			.getLogger(CFGDumpRunnable.class);

	public static final String GRAPHML_FORMAT = "graphml";
	public static final String GML_FORMAT = "gml";

	private Vertex functionNode;
	private OrientGraphFactory factory;
	private Path targetDirectory;
	private String format;

	public CFGDumpRunnable(OrientGraphFactory factory, Vertex functionNode,
			Path dir, String format)
	{
		this.factory = factory;
		this.functionNode = functionNode;
		this.targetDirectory = dir;
		this.format = format;
	}

	@Override
	public void run()
	{
		Object functionId = functionNode.getId();
		OrientGraphNoTx graph = factory.getNoTx();
		try
		{
			CFGCreator cfgCreator = new CFGCreator(graph);
			Graph cfg = cfgCreator.createCFG(functionNode);
			Path targetFile = getTargetFile(
					functionId.toString().split(":")[1]);
			dumpGraph(cfg, targetFile);
			logger.info("Writing control flow graph of function " + functionId
					+ " to file " + targetFile + ".");

		} catch (FileAlreadyExistsException e)
		{
			logger.warn("Skipping function " + functionId + ". File exists: "
					+ e.getMessage());
		} catch (IOException e)
		{
			logger.error("Skipping function " + functionId + ". IO Exception: "
					+ e.getMessage());
			e.printStackTrace();
		} finally
		{
			graph.shutdown();
		}
	}

	private Path getTargetFile(String function)
	{
		String filename = function + "." + this.format;
		Path dest = Paths.get(targetDirectory.toString(), filename);
		return dest.toAbsolutePath().normalize();
	}

	private void dumpGraph(Graph graph, Path path) throws IOException
	{
		OutputStream out = Files.newOutputStream(path,
				StandardOpenOption.CREATE_NEW);
		switch (format)
		{
		case GML_FORMAT:
			GMLWriter.outputGraph(graph, out);
			break;
		case GRAPHML_FORMAT:
			GraphMLWriter.outputGraph(graph, out);
			break;
		}
		out.close();
	}

}
