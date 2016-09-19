package bjoern.plugins.vsa.data;

import bjoern.plugins.vsa.structures.Bool3;

public class Flag extends GenericDataObject<Bool3>
{
	public Flag(String identifier, Bool3 value)
	{
		super(identifier, value);
	}

	@Override
	public Flag copy()
	{
		return new Flag(getIdentifier(), read());
	}
}
