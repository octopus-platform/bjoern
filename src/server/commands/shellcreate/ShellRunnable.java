package server.commands.shellcreate;

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

import org.codehaus.groovy.tools.shell.ExitNotification;

import com.orientechnologies.common.log.OLogManager;

public class ShellRunnable implements Runnable
{

	private int port;
	private String dbName;
	private ServerSocket serverSocket;
	private BjoernGremlinShell bjoernGremlinShell;
	private Socket clientSocket;
	private PrintWriter clientWriter;
	private BufferedReader clientReader;

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

		OLogManager.instance().warn(this, "Shell closed");
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	private void createGremlinShell() throws IOException
	{
		bjoernGremlinShell = new BjoernGremlinShell(dbName);
	}

	private void createLocalListeningSocket() throws IOException
	{
		InetAddress bindAddr = InetAddress.getLoopbackAddress();
		serverSocket = new ServerSocket(port, 10, bindAddr);
	}

	private void processClients() throws IOException
	{
		while (true)
		{
			acceptNewClient();
			try
			{
				handleClient();
			}
			catch (ExitNotification ex)
			{
				clientSocket.close();
				break;
			}
		}

		serverSocket.close();
	}

	private void acceptNewClient() throws IOException
	{
		clientSocket = serverSocket.accept();
		initClientWriter();
		initClientReader();

		System.out.println("Client accepted");
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
			Object evalResult = bjoernGremlinShell.execute(line);
			sendResultToClient(evalResult);
		}
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
