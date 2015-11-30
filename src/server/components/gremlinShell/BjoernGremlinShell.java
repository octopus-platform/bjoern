package server.components.gremlinShell;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.groovy.Gremlin;

import groovy.lang.GroovyShell;
import server.Constants;

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
		openDatabaseConnection(dbName);
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
