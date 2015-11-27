package server.components.gremlinShell;

import org.codehaus.groovy.control.CompilerConfiguration;

public class BjoernCompilerConfiguration extends CompilerConfiguration
{

	public BjoernCompilerConfiguration()
	{
		this.setScriptBaseClass(BjoernScriptBase.class.getName());
	}

}