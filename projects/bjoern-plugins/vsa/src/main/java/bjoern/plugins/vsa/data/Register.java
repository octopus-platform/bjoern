package bjoern.plugins.vsa.data;

import bjoern.plugins.vsa.domain.ValueSet;

public class Register extends WriteableDataObject<ValueSet> {

	public Register(Object identifier, ValueSet value) {
		super(identifier, value);
	}

	@Override
	public DataObject<ValueSet> copy() {
		return new Register(getIdentifier(), read());
	}
}
