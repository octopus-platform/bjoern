package bjoern.plugins.vsa.data;

import bjoern.plugins.vsa.domain.ValueSet;

public class Register extends GenericDataObject<ValueSet>
{
	public Register(String identifier, ValueSet value)
	{
		super(identifier, value);
	}

	@Override
	public DataObject<ValueSet> copy()
	{
		return new Register(getIdentifier(), read());
	}
}
