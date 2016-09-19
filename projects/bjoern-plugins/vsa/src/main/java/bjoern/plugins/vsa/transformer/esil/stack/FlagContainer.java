package bjoern.plugins.vsa.transformer.esil.stack;

import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;

public class FlagContainer implements ESILStackItem
{

	private final DataObject<Bool3> dataObject;

	public FlagContainer(DataObject<Bool3> dataObject)
	{
		this.dataObject = dataObject;
	}

	public DataObject<Bool3> getFlag()
	{
		return this.dataObject;
	}

	public ValueSet getValue()
	{
		Bool3 value = dataObject.read();
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
}
