package server.components.gremlinShell;

import java.io.IOException;
import java.nio.file.Path;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyShell;
import server.components.gremlinShell.fileWalker.SourceFileListener;

public class GroovyFileLoader extends SourceFileListener
{

	private GroovyShell groovyShell;

	public void setGroovyShell(GroovyShell groovysh)
	{
		this.groovyShell = groovysh;
	}

	@Override
	public void visitFile(Path filename)
	{
		try
		{
			groovyShell.evaluate(filename.toFile());
		} catch (CompilationFailedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
