package bjoern.plugins.vsa.data;

import bjoern.plugins.vsa.structures.Bool3;

public class Flag extends WriteableDataObject<Bool3> {

	public Flag(Object identifier, Bool3 value) {
		super(identifier, value);
	}

	@Override
	public Flag copy() {
		return new Flag(getIdentifier(), read());
	}
}
