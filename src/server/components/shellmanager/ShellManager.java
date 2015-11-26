package server.components.shellmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import server.components.gremlinShell.BjoernGremlinShell;

public class ShellManager
{
	private static final int MAX_SHELLS = 1024;
	private static final int FIRST_PORT = 6000;

	static BjoernGremlinShell[] shells;
	static Semaphore shellsMutex = new Semaphore(1);

	static
	{
		shells = new BjoernGremlinShell[MAX_SHELLS];
	}

	/**
	 * @return the shell's port number
	 * @throws InterruptedException
	 * */

	public static int createNewShell(String dbName)
	{

		try
		{
			shellsMutex.acquire();
		}
		catch (InterruptedException e)
		{
			// If interrupted at this point, we have not made
			// any changes so it should be save to exit.
			throw new RuntimeException("Interrupted during shell creation");
		}

		int port = getFirstFreePort();
		BjoernGremlinShell shell = new BjoernGremlinShell(dbName);
		shell.setPort(port);
		shells[port - FIRST_PORT] = shell;
		shellsMutex.release();

		shell.initShell();

		return port;
	}

	private static int getFirstFreePort()
	{
		for (int i = 0; i < MAX_SHELLS; i++)
		{
			if (shells[i] == null)
				return i + FIRST_PORT;
		}

		throw new RuntimeException("No more free slots for your shell");
	}

	public static BjoernGremlinShell getShellForPort(int port)
	{
		int index = port - FIRST_PORT;
		if (index >= MAX_SHELLS || shells[index] == null)
			throw new RuntimeException(String.format(
					"Invalid shell for port: %d", port));

		return shells[index];
	}

	public static void destroyShell(int port)
	{
		int index = port - FIRST_PORT;

		if (index >= MAX_SHELLS || shells[index] == null)
			throw new RuntimeException(String.format(
					"Request to delete non-existent shell: %d", port));

		shells[index] = null;
	}

	public static List<BjoernGremlinShell> getActiveShells()
	{
		List<BjoernGremlinShell> retval = new LinkedList<BjoernGremlinShell>();
		for (int i = 0; i < MAX_SHELLS; i++)
		{
			if (shells[i] != null)
				retval.add(shells[i]);
		}
		return retval;
	}

}
