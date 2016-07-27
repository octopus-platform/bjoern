package bjoern.r2interface.creators;

import bjoern.structures.annotations.Flag;
import org.json.JSONObject;

public class RadareFlagCreator
{
	public static Flag createFromJSON(JSONObject jsonFunction)
	{
		Long address = jsonFunction.getLong("offset");
		String name = jsonFunction.getString("name");
		Long size = jsonFunction.getLong("size");

		return new Flag.Builder(address).withValue(name).withLenght(size).build();
	}
}
