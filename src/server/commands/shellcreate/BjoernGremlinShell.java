package server.commands.shellcreate;

import org.codehaus.groovy.tools.shell.Groovysh;

import com.tinkerpop.gremlin.Imports;
import com.tinkerpop.gremlin.groovy.Gremlin;

public class BjoernGremlinShell
{
	Groovysh groovysh = new Groovysh();

	public BjoernGremlinShell()
	{
		performInitialImports();
		Gremlin.load();

	}

	private void performInitialImports()
	{

		for (String imps : Imports.getImports())
		{
			groovysh.execute("import " + imps);
		}
		groovysh.execute("import com.tinkerpop.gremlin.Tokens.T");
		groovysh.execute("import com.tinkerpop.gremlin.groovy.*");
		groovysh.execute("import groovy.grape.Grape");

	}

	public Object execute(String line)
	{
		return groovysh.execute(line);
	}

}
