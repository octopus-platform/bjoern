package server.components.gremlinShell;

import java.io.IOException;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.groovy.Gremlin;

import groovy.lang.GroovyShell;
import server.Constants;
import server.components.gremlinShell.fileWalker.OrderedWalker;
import server.components.gremlinShell.fileWalker.SourceFileWalker;

public class BjoernGremlinShell
{

	private GroovyShell shell;
	private int port;
	private final String dbName;

	static
	{
		Gremlin.load();
	}

	public BjoernGremlinShell(String dbName)
	{
		this.dbName = dbName;
	}

	public void initShell()
	{
		this.shell = new GroovyShell(new BjoernCompilerConfiguration());
		loadQueryLibrary();
		openDatabaseConnection(dbName);
	}

	private void loadQueryLibrary()
	{
		try
		{
			loadRecursively(Constants.QUERY_LIB_DIR);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadRecursively(String queryLibDir) throws IOException
	{
		SourceFileWalker walker = new OrderedWalker();
		GroovyFileLoader listener = new GroovyFileLoader();
		listener.setGroovyShell(shell);

		walker.setFilenameFilter("*.groovy");
		walker.addListener(listener);
		walker.walk(new String[] { queryLibDir });
	}

	private void openDatabaseConnection(String dbName)
	{
		// TODO: We should check whether the database exists

		OrientGraphNoTx g = new OrientGraphNoTx(
				Constants.PLOCAL_REL_PATH_TO_DBS + dbName);
		this.shell.setVariable("g", g);
	}

	public Object execute(String line)
	{
		if (line.equals("reload"))
		{
			loadQueryLibrary();
			return new String("");
		}

		try
		{
			return shell.evaluate(line);
		} catch (Exception ex)
		{
			return String.format("[%s] %s", ex.getClass().getSimpleName(),
					ex.getMessage());
		}
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getDbName()
	{
		return dbName;
	}
}
