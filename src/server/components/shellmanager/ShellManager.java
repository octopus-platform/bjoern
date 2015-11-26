package server.components.shellmanager;

import server.components.gremlinShell.BjoernGremlinShell;

public class ShellManager
{
	private static final int MAX_SHELLS = 1024;
	private static final int FIRST_PORT = 6000;

	static BjoernGremlinShell[] shells;

	static
	{
		shells = new BjoernGremlinShell[MAX_SHELLS];
	}

	/**
	 * @return the shell's port number
	 * */

	public static int createNewShell(String dbName)
	{
		int port = getFirstFreePort();
		BjoernGremlinShell shell = new BjoernGremlinShell(dbName);
		shells[port - FIRST_PORT] = shell;
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

		if (index >= MAX_SHELLS || shells[index] != null)
			throw new RuntimeException(String.format(
					"Request to delete non-existent shell: %d", port));

		shells[index] = null;
	}

}
