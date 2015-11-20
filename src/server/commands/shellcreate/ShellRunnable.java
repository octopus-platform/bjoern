package server.commands.shellcreate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.orientechnologies.common.log.OLogManager;

public class ShellRunnable implements Runnable
{

	private int port;
	private ServerSocket serverSocket;

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public void run()
	{
		try
		{
			createLocalListeningSocket();
			processClients();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		OLogManager.instance().warn(this, "Shell closed");
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
		Socket clientSocket = serverSocket.accept();
		OLogManager.instance().warn(this, "Client accepted");
		clientSocket.close();
		// }
	}
}
