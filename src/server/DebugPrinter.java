package server;

import com.orientechnologies.common.log.OLogManager;

public class DebugPrinter
{

	public static void print(String str, Object obj)
	{
		OLogManager.instance().warn(obj, str);
	}
}
