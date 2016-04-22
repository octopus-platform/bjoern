package octopus.server.commands.uploadfile;

import java.io.FileOutputStream;
import java.util.Base64;

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

		byte[] decoded = Base64.getMimeDecoder().decode(iRequest.content);

		FileOutputStream file = new FileOutputStream("/tmp/lala");
		file.write(decoded);
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
