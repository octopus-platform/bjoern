package server.components.gremlinShell.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class BjoernClientWriter extends BufferedWriter
{

	public BjoernClientWriter(Writer out)
	{
		super(out);
	}

	public void writeMessage(String message) throws IOException
	{
		this.write(message);
		this.newLine();
		this.write("\0");
		this.newLine();
		this.flush();
	}

	public void writeResult(Object result) throws IOException
	{
		if (result == null)
		{
			write("\0");
			newLine();
		} else if (result instanceof Iterable)
		{
			Iterable<?> iterable = (Iterable<?>) result;
			for (Object obj : iterable)
			{
				write(obj.toString());
				newLine();
			}
			write("\0");
			newLine();
		} else
		{
			writeMessage(result.toString());
		}
		this.flush();
	}

}
