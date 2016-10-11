package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.memrgn.LocalRegion;
import bjoern.plugins.vsa.domain.memrgn.MemoryRegion;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class PokeCommand implements ESILCommand {

	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ValueSet value1 = stack.pop().execute(stack, env).getValue();
		ValueSet value2 = stack.pop().execute(stack, env).getValue();
		if (env == null) {
			return null;
		}
		StridedInterval addresses = getValueOfLocalRegion(value1);
		ValueSet tmp = env.getRegister("rbp");
		if (tmp == null) {
			return null;
		}
		StridedInterval rbp = getValueOfLocalRegion(tmp);
		addresses = addresses.sub(rbp);
		if (addresses.isSingletonSet()) {
			for (long address : addresses.values()) {
				env.setLocalVariable(address, value2);
			}
		}
		// this command returns nothing/no item is pushed on the stack
		return null;
	}

	private StridedInterval getValueOfLocalRegion(ValueSet valueSet) {
		if (valueSet.isTop()) {
			return StridedInterval.getTop(valueSet.getDataWidth());
		}
		for (MemoryRegion region : valueSet.getRegions()) {
			if (region instanceof LocalRegion) {
				return valueSet.getValueOfRegion(region);
			}
		}
		return StridedInterval.getBottom(valueSet.getDataWidth());
	}
}
