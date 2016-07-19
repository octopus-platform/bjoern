package bjoern.r2interface.creators;

import bjoern.structures.annotations.Flag;
import org.json.JSONObject;

public class RadareFlagCreator
{
	public static Flag createFromJSON(JSONObject jsonFunction)
	{
		Flag retval = new Flag(jsonFunction.getLong("offset"));
		retval.setValue(jsonFunction.getString("name"));
		retval.setLength(jsonFunction.getLong("size"));

		return retval;
	}
}
