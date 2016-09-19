package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.domain.ValueSet;

public class RegisterContainer implements ESILStackItem
{

	private final DataObject<ValueSet> dataObject;

	public RegisterContainer(DataObject<ValueSet> register)
	{
		this.dataObject = register;
	}


	public DataObject<ValueSet> getRegister()
	{
		return this.dataObject;
	}

	@Override
	public ValueSet getValue()
	{
		return dataObject.read();
	}
}
