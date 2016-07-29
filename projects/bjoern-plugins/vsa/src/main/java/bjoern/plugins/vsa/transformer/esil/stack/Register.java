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
		this(register.getIdentifier(), register.getValue());
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

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Register))
		{
			return false;
		}

		Register other = (Register) o;
		return identifier.equals(other.identifier) && value.equals(other.value);
	}

	@Override
	public int hashCode()
	{
		int result = identifier.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	public void setValue(ValueSet value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Value must not be null");
		}
		this.value = value;
	}

	public String getIdentifier()
	{
		return identifier;
	}
}
