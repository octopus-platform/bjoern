package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.FlagContainer;
import bjoern.plugins.vsa.transformer.esil.stack.RegisterContainer;

import java.util.Deque;

public class AssignmentCommand implements ESILCommand {

	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {

		ESILStackItem item = stack.pop().execute(stack, env);

		if (item instanceof RegisterContainer) {
			RegisterContainer registerContainer = (RegisterContainer) item;
			DataObject<ValueSet> register = registerContainer.getRegister();
			env.setRegister(register.getIdentifier(),
					stack.pop().execute(stack, env).getValue());
		} else if (item instanceof FlagContainer) {
			FlagContainer flagContainer = (FlagContainer) item;
			DataObject<Bool3> flag = flagContainer.getFlag();
			ValueSet valueSet = stack.pop().execute(stack, env).getValue();
			if (valueSet.isGlobal()) {
				StridedInterval stridedInterval = valueSet
						.getValueOfGlobalRegion();
				if (stridedInterval.isZero()) {
					env.setFlag(flag.getIdentifier(), Bool3.FALSE);
				} else if (stridedInterval.isOne()) {
					env.setFlag(flag.getIdentifier(), Bool3.TRUE);
				} else {
					env.setFlag(flag.getIdentifier(), Bool3.MAYBE);
				}
			} else {
				throw new ESILTransformationException(
						"Error while executing assignment command: Cannot "
								+ "assign "
								+ valueSet + " to flag");
			}
		} else {
			throw new ESILTransformationException(
					"Error while executing assignment command");
		}
		return null;
	}
}
