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

	private void writeLine(String line) throws IOException
	{
		if (line != null)
		{
			this.write(line);
			this.newLine();
		}
	}

	private void writeEndOfMessage() throws IOException
	{
		write("\0");
		newLine();
	}

	public void writeMessage(String message) throws IOException
	{
		writeLine(message);
		writeEndOfMessage();
		flush();
	}

	public void writeResult(Object result) throws IOException
	{
		if (result == null)
		{
			writeEndOfMessage();
			this.flush();
		} else if (result instanceof Iterable)
		{
			Iterable<?> iterable = (Iterable<?>) result;
			for (Object obj : iterable)
			{
				if (obj != null)
				{
					writeLine(obj.toString());
				}
			}
			writeEndOfMessage();
			this.flush();
		} else
		{
			writeMessage(result.toString());
		}
	}

}
