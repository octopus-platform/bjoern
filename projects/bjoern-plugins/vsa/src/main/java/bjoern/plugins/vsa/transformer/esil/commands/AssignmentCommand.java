package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.FlagContainer;
import bjoern.plugins.vsa.transformer.esil.stack.RegisterContainer;

public class AssignmentCommand implements ESILCommand
{
	@Override
	public void execute(AbstractEnvironment env, ESILStack stack)
	{
		ESILStackItem item = stack.pop();
		if (item instanceof RegisterContainer)
		{
			RegisterContainer registerContainer = (RegisterContainer) item;
			DataObject<ValueSet> register = registerContainer.getRegister();
			register.write(stack.popValueSet());
		} else if (item instanceof FlagContainer)
		{
			FlagContainer flagContainer = (FlagContainer) item;
			DataObject<Bool3> flag = flagContainer.getFlag();
			ValueSet valueSet = stack.popValueSet();
			if (valueSet.isGlobal())
			{
				StridedInterval stridedInterval = valueSet.getValueOfGlobalRegion();
				if (stridedInterval.isZero())
				{
					flag.write(Bool3.FALSE);
				} else if (stridedInterval.isOne())
				{
					flag.write(Bool3.TRUE);
				} else
				{
					flag.write(Bool3.MAYBE);
				}
			} else
			{
				throw new ESILTransformationException(
						"Error while executing assignment command: Cannot assign " + valueSet + " to flag");
			}
		} else
		{
			throw new ESILTransformationException("Error while executing assignment command");
		}
	}
}
