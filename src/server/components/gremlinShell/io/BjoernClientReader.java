package server.components.gremlinShell.io;

import java.io.IOException;
import java.io.Reader;

public class BjoernClientReader extends Reader
{
	private Reader in;

	public BjoernClientReader(Reader in)
	{
		this.in = in;
	}

	@Override
	public void close() throws IOException
	{
		in.close();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return in.read(cbuf, off, len);
	}

	public String readMessage() throws IOException
	{
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = in.read()) != -1)
		{
			if (c == '\0')
			{
				// Messages end with "...\n\0\n".
				// Skip the last newline and remove the other.
				in.skip(1);
				builder.setLength(builder.length() - 1);
				String message = builder.toString();
				return message;
			} else
			{
				builder.append((char) c);
			}
		}
		return null;
	}

}
