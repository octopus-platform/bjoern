package server.components.gremlinShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import server.components.gremlinShell.io.BjoernClientReader;
import server.components.gremlinShell.io.BjoernClientWriter;
import server.components.shellmanager.ShellManager;

public class ShellRunnable implements Runnable
{
	private String dbName;
	private ServerSocket serverSocket;
	private BjoernGremlinShell bjoernGremlinShell;
	private Socket clientSocket;
	private BjoernClientWriter clientWriter;
	private BjoernClientReader clientReader;

	private boolean listen = true;

	@Override
	public void run()
	{
		try
		{
			createGremlinShell();
			createLocalListeningSocket();
			processClients();
		} catch (IOException e)
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
		clientReader = new BjoernClientReader(new InputStreamReader(in));
	}

	private void initClientWriter() throws IOException
	{
		OutputStream out = clientSocket.getOutputStream();
		clientWriter = new BjoernClientWriter(new OutputStreamWriter(out));
	}

	private void handleClient() throws IOException
	{

		String message;
		while ((message = clientReader.readMessage()) != null)
		{
			if (message.equals("quit"))
			{
				listen = false;
				clientWriter.writeMessage("bye");
				break;
			} else
			{
				Object evalResult;
				try
				{
					evalResult = bjoernGremlinShell.execute(message);
					clientWriter.writeResult(evalResult);
				} catch (Exception ex)
				{
					clientWriter.writeMessage(ex.getMessage());
				}
			}

		}
		clientSocket.close();
	}
}
