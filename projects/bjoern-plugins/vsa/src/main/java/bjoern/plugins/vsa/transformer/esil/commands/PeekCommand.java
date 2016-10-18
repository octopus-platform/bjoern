package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.memrgn.LocalRegion;
import bjoern.plugins.vsa.domain.memrgn.MemoryRegion;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;
import java.util.Set;

public class PeekCommand implements ESILCommand {

	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ValueSet offsets = getOperand(stack, env).getValue();

		Long spOffset = getSPOffset(offsets);
		if (spOffset == null) {
			return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
		}
		ValueSet value = env.getBPVariable(spOffset);
		if (value == null) {
			return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
		} else {
			return new ValueSetContainer(value);
		}
	}

	private Long getSPOffset(final ValueSet offsets) {
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

}
