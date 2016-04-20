package bjoern.r2interface.creators;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils
{
	public static Long getLongFromObject(JSONObject block, String key)
	{
		try
		{
			Long val = block.getLong(key);
			return val;
		} catch (JSONException ex)
		{
			return null;
		}
	}

	public static String getStringFromObject(JSONObject jsonObj, String key)
	{
		try
		{
			String val = jsonObj.getString(key);
			return val;
		} catch (JSONException ex)
		{
			return null;
		}
	}
}
