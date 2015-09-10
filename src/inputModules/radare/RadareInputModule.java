package inputModules.radare;

import inputModules.InputModule;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import structures.Function;

public class RadareInputModule implements InputModule
{

	Radare radare = new Radare();

	@Override
	public void initialize(String filename)
	{
		radare.loadBinary(filename);
		radare.analyzeBinary();
	}

	@Override
	public List<Long> getFunctionAddresses()
	{
		List<BigInteger> retval = radare.getFunctionAddresses();
		return retval.stream().map(x -> x.longValue())
				.collect(Collectors.toList());
	}

	@Override
	public Function getFunctionAtAddress(Long addr)
	{
		JSONObject jsonFunction = radare.getJSONFunctionAt(addr);
		if (jsonFunction == null)
			return null;

		Function function = RadareFunctionCreator.createFromJSON(jsonFunction);

		return function;
	}

}
