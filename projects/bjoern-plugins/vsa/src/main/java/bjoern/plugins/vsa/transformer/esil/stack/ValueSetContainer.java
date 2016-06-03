package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.domain.ValueSet;

public class ValueSetContainer implements ESILStackItem<ValueSet>
{

	private final ValueSet valueSet;

	public ValueSetContainer(ValueSet valueSet) {
		this.valueSet = valueSet;
	}

	@Override
	public ValueSet getValue()
	{
		return valueSet;
	}
}
