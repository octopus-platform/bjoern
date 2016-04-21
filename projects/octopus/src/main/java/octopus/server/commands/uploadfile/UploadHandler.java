package octopus.server.commands.uploadfile;

import java.io.FileOutputStream;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class UploadHandler extends OServerCommandAbstract {

	public UploadHandler(final OServerCommandConfiguration iConfiguration)
	{
	}

	@Override
	public boolean execute(OHttpRequest iRequest, OHttpResponse iResponse) throws Exception
	{

		FileOutputStream file = new FileOutputStream("/tmp/lala");
		file.write(iRequest.content.getBytes());
		file.close();

		iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null,
				"", null);
		return false;
	}

	@Override
	public String[] getNames()
	{
		return new String[] {"POST|uploadfile/"};
	}

}
