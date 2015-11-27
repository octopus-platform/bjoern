package server.components.gremlinShell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.gremlin.Imports;
import com.tinkerpop.gremlin.groovy.Gremlin;

import groovy.lang.GroovyShell;
import server.Constants;
import server.components.gremlinShell.fileWalker.OrderedWalker;
import server.components.gremlinShell.fileWalker.SourceFileWalker;

public class BjoernGremlinShell
{

	private static final List<String> imports = new ArrayList<String>();

	static
	{
		imports.addAll(Imports.getImports());
		imports.add("com.tinkerpop.gremlin.Tokens.T");
		imports.add("com.tinkerpop.gremlin.groovy.*");
		imports.add("groovy.grape.Grape");

		Gremlin.load();
	}

	private GroovyShell shell;
	private int port;
	private final String dbName;

	public BjoernGremlinShell(String dbName)
	{
		this.dbName = dbName;
	}

	public void initShell()
	{
		this.shell = new GroovyShell();
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

	private static String importStatements()
	{
		StringBuilder importStatements = new StringBuilder();
		for (String imp : imports)
		{
			importStatements.append("import ");
			importStatements.append(imp);
			importStatements.append('\n');
		}
		return importStatements.toString();
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
			String script = importStatements() + "\n" + line;
			return shell.evaluate(script);
		} catch (Exception ex)
		{
			return ex.getMessage();
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
