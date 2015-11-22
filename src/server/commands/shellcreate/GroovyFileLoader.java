package server.commands.shellcreate;

import java.nio.file.Path;

import org.codehaus.groovy.tools.shell.Groovysh;

import server.fileWalker.SourceFileListener;

public class GroovyFileLoader extends SourceFileListener
{

	private Groovysh groovysh;

	public void setGroovyShell(Groovysh groovysh)
	{
		this.groovysh = groovysh;
	}

	@Override
	public void visitFile(Path filename)
	{
		String cmd = String.format("load %s", filename);
		groovysh.execute(cmd);
	}

	@Override
	public void initialize()
	{
	}

	@Override
	public void shutdown()
	{
	}

	@Override
	public void preVisitDirectory(Path dir)
	{
	}

	@Override
	public void postVisitDirectory(Path dir)
	{
	}

}
