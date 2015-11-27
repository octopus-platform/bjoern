package server.components.gremlinShell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import server.components.shellmanager.ShellManager;

public class ShellRunnable implements Runnable
{
	private String dbName;
	private ServerSocket serverSocket;
	private BjoernGremlinShell bjoernGremlinShell;
	private Socket clientSocket;
	private PrintWriter clientWriter;
	private BufferedReader clientReader;

	private boolean listen = true;

	@Override
	public void run()
	{
		try
		{
			createGremlinShell();
			createLocalListeningSocket();
			processClients();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	private void createGremlinShell() throws IOException
	{
		int port = ShellManager.createNewShell(dbName);
		bjoernGremlinShell = ShellManager.getShellForPort(port);
	}

	private void createLocalListeningSocket() throws IOException
	{
		InetAddress bindAddr = InetAddress.getLoopbackAddress();
		serverSocket = new ServerSocket(bjoernGremlinShell.getPort(), 10,
				bindAddr);
	}

	private void processClients() throws IOException
	{
		while (listen)
		{
			acceptNewClient();
			handleClient();
		}
		ShellManager.destroyShell(bjoernGremlinShell.getPort());
		serverSocket.close();
	}

	private void acceptNewClient() throws IOException
	{
		clientSocket = serverSocket.accept();
		initClientWriter();
		initClientReader();
	}

	private void initClientReader() throws IOException
	{
		InputStream in = clientSocket.getInputStream();
		clientReader = new BufferedReader(new InputStreamReader(in));
	}

	private void initClientWriter() throws IOException
	{
		OutputStream outputStream = clientSocket.getOutputStream();
		clientWriter = new PrintWriter(outputStream);
	}

	private void handleClient() throws IOException
	{

		String line;
		while ((line = clientReader.readLine()) != null)
		{
			if (line.equals("quit"))
			{
				listen = false;
				sendResultToClient("bye");
				sendResultToClient("\0");
				break;
			} else
			{
				Object evalResult;
				try
				{
					evalResult = bjoernGremlinShell.execute(line);
					sendResultToClient(evalResult);
				} catch (Exception ex)
				{
					sendResultToClient(ex.getMessage());
				}
				sendResultToClient("\0");
			}
		}
		clientSocket.close();
	}

	private void sendResultToClient(Object result)
	{
		if (result == null)
			return;

		if (result instanceof Iterable)
		{
			Iterable<?> iterable = (Iterable<?>) result;
			Iterator<?> it = iterable.iterator();
			while (it.hasNext())
			{
				Object obj = it.next();
				sendResultToClient(obj);
			}
		}
		else
		{
			clientWriter.println(result.toString());
			clientWriter.flush();
		}
	}
}
