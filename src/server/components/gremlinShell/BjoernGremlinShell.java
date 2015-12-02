package server.components.gremlinShell;

import groovy.lang.GroovyShell;
import server.Constants;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.groovy.Gremlin;

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

	private void registerMethodMissingHandler()
	{
		String cmd = "GremlinGroovyPipeline.metaClass.methodMissing =";
		cmd += "{String name, args -> Gremlin.compose(delegate, \"$name\"(args))}";
		execute(cmd);
	}

	public void initShell()
	{
		this.shell = new GroovyShell(new BjoernCompilerConfiguration());
		openDatabaseConnection(dbName);
		registerMethodMissingHandler();
	}

	private void openDatabaseConnection(String dbName)
	{
		// TODO: We should check whether the database exists

		OrientGraphNoTx g = new OrientGraphNoTx(
				Constants.PLOCAL_REL_PATH_TO_DBS + dbName);
		this.shell.setVariable("g", g);
	}

	public Object execute(String code)
	{
		try
		{
			return shell.evaluate(code);
		}
		catch (Exception ex)
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

	public GroovyShell getShell()
	{
		return shell;
	}
}
