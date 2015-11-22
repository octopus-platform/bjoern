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

import com.orientechnologies.common.log.OLogManager;

public class ShellRunnable implements Runnable
{

	private int port;
	private ServerSocket serverSocket;
	private BjoernGremlinShell bjoernGremlinShell;
	private Socket clientSocket;

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

	private void createGremlinShell() throws IOException
	{
		bjoernGremlinShell = new BjoernGremlinShell();
	}

	private void createLocalListeningSocket() throws IOException
	{
		InetAddress bindAddr = InetAddress.getLoopbackAddress();
		serverSocket = new ServerSocket(port, 10, bindAddr);
	}

	private void processClients() throws IOException
	{
		// while (true)
		// {
		clientSocket = serverSocket.accept();
		OLogManager.instance().warn(this, "Client accepted");
		handleClient();

		// }

		serverSocket.close();
	}

	private void handleClient() throws IOException
	{
		InputStream in = clientSocket.getInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
		OutputStream outputStream = clientSocket.getOutputStream();
		PrintWriter printWriter = new PrintWriter(outputStream);

		String line;

		while ((line = bReader.readLine()) != null)
		{
			Object evalResult = bjoernGremlinShell.execute(line);
			System.out.println(evalResult);
			printWriter.println(evalResult.toString());
			printWriter.flush();
		}
	}
}
