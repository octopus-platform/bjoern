package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.domain.ValueSet;

public class Register implements ESILStackItem<ValueSet>
{

	private final String identifier;
	private ValueSet value;

	public Register(String identifier, ValueSet value)
	{
		this.identifier = identifier;
		setValue(value);
	}

	public Register(Register register)
	{
		this(register.getIdentifier(), ValueSet.copy(register.getValue()));
	}

	@Override
	public ValueSet getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return identifier + " = " + value;
	}

	public void setValue(ValueSet value)
	{
		this.value = value;
	}

	public String getIdentifier()
	{
		return identifier;
	}
}
