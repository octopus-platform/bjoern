package server.components.cfgdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import server.Constants;

public class CFGDumpService
{

	private OrientGraphFactory factory;
	private OpenOption[] openOptions;
	private ExecutorService executor;
	private Path targetDirectory;

	public CFGDumpService(String databaseName, Path dest,
			OpenOption[] openOptions, int nThreads) throws IOException
	{
		this.factory = new OrientGraphFactory(
				Constants.PLOCAL_REL_PATH_TO_DBS + databaseName).setupPool(1,
						10);
		this.openOptions = openOptions;
		this.executor = Executors.newFixedThreadPool(nThreads);
		this.targetDirectory = Paths.get(dest.toString(), databaseName, "cfg");
		Files.createDirectories(targetDirectory);
	}

	public void dumpCFG(Vertex functionNode)
	{
		CFGDumpRunnable runnable = new CFGDumpRunnable(factory, functionNode,
				targetDirectory, openOptions);

		executor.execute(runnable);
	}

	public void shutDown()
	{
		executor.shutdown();
		factory.close();
	}

	public void awaitTermination() throws InterruptedException
	{
		while (!executor.isTerminated())
		{
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
	}

}