package exporters.radare.inputModule;

// Adapted from:
// https://github.com/radare/radare2-bindings/blob/master/r2pipe/java/org/radare/r2pipe/R2Pipe.java

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import exporters.radare.StreamGobbler;

class R2Pipe
{
	public final String R2_LOC = "radare2";
	private Process process;
	private OutputStream stdin;
	private InputStream stdout;
	private StreamGobbler errorGobbler;

	public R2Pipe(String filename) throws IOException
	{
		spawnR2Process(filename);
	}

	private void spawnR2Process(String filename) throws IOException
	{

		try
		{
			process = Runtime.getRuntime().exec(R2_LOC + " -q0 " + filename);
		}
		catch (IOException e)
		{
			throw new IOException("Cannot find `radare2` on path.");
		}

		stdin = process.getOutputStream();
		stdout = process.getInputStream();

		errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
		errorGobbler.start();

		readUpToZeroByte();
	}

	public String cmd(String cmd) throws IOException
	{
		cmd += "\n";

		stdin.write((cmd).getBytes());
		stdin.flush();

		return readUpToZeroByte();
	}

	private String readUpToZeroByte() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[1];
		while (stdout.read(b) == 1)
		{
			if (b[0] == '\0')
				break;
			sb.append((char) b[0]);
		}
		return sb.toString();
	}

	public void quit() throws Exception
	{
		cmd("q");
	}

}