package bjoern.r2interface;

// Adapted from:
// http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread
{
	InputStream is;
	String type;

	public StreamGobbler(InputStream is, String type)
	{
		this.is = is;
		this.type = type;
	}

	@Override
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
