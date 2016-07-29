package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;

public class Flag implements ESILStackItem<ValueSet>
{

	private final String identifier;
	private Bool3 value;

	public Flag(String identifier, Bool3 value)
	{
		this.identifier = identifier;
		setValue(value);
	}

	public Flag(Flag flag)
	{
		this(flag.getIdentifier(), flag.getBooleanValue());
	}

	public String getIdentifier()
	{

		return identifier;
	}

	@Override
	public ValueSet getValue()
	{
		switch (value)
		{
			case FALSE:
				return ValueSet.newGlobal(StridedInterval.getSingletonSet(0, DataWidth.R1));
			case TRUE:
				return ValueSet.newGlobal(StridedInterval.getSingletonSet(1, DataWidth.R1));
			case MAYBE:
			default:
				return ValueSet.newGlobal(StridedInterval.getTop(DataWidth.R1));
		}
	}

	@Override
	public String toString()
	{
		return identifier + " = " + value;
	}


	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Flag))
		{
			return false;
		}

		Flag other = (Flag) o;
		return identifier.equals(other.identifier) && value.equals(other.value);
	}

	@Override
	public int hashCode()
	{
		int result = identifier.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	public void setValue(Bool3 value)
	{
		this.value = value;
	}

	public Bool3 getBooleanValue()
	{
		return value;
	}
}
