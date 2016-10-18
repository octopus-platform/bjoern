package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.memrgn.LocalRegion;
import bjoern.plugins.vsa.domain.memrgn.MemoryRegion;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;
import java.util.Set;

public class PokeCommand implements ESILCommand {

	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ValueSet offsets = getOperand(stack, env).getValue();
		ValueSet value = getOperand(stack, env).getValue();
		ValueSet basePointer = env.getBasePointer();
		if (basePointer == null) {
			return null;
		}
		offsets = offsets.sub(basePointer);
		Long offset = getLocalValue(offsets);
		if (offset != null) {
			env.setLocalVariable(offset, value);
		}
		// this command returns nothing/no item is pushed on the stack
		return null;
	}

	private Long getLocalValue(final ValueSet offsets) {
		Set<MemoryRegion> regions = offsets.getRegions();
		if (regions.size() != 1) {
			return null;
		}
		MemoryRegion region = regions.iterator().next();
		if (!(region instanceof LocalRegion)) {
			return null;
		}
		StridedInterval interval = offsets.getValueOfRegion(region);
		if (!interval.isSingletonSet()) {
			return null;
		}
		return interval.values().iterator().next();
	}

	protected ESILStackItem getOperand(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		return stack.pop().execute(stack, env);
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
