package server.components.cfgdump;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

public class CFGDumpRunnable implements Runnable
{
	private static final Logger logger = LoggerFactory
			.getLogger(CFGDumpRunnable.class);

	private Vertex functionNode;
	private OrientGraphFactory factory;
	private Path targetDirectory;
	private OpenOption[] openOptions;

	public CFGDumpRunnable(OrientGraphFactory factory, Vertex functionNode,
			Path dir, OpenOption[] openOptions)
	{
		this.factory = factory;
		this.functionNode = functionNode;
		this.targetDirectory = dir;
		this.openOptions = openOptions;
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
			Path targetFile = getTargetFile(functionId.toString());
			dumpGraph(cfg, targetFile);
			logger.info("Writing control flow graph of function " + functionId
					+ " to file " + targetDirectory + ".");

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
		String filename = function + ".graphml";
		Path dest = Paths.get(targetDirectory.toString(), filename);
		return dest.toAbsolutePath().normalize();
	}

	private void dumpGraph(Graph graph, Path path) throws IOException
	{
		OutputStream out = Files.newOutputStream(path, openOptions);
		GraphMLWriter.outputGraph(graph, out);
		out.close();
	}

}
