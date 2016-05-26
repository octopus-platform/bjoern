package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.domain.region.GlobalRegion;
import bjoern.plugins.vsa.domain.region.LocalRegion;
import bjoern.plugins.vsa.domain.region.MemoryRegion;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValueSet
{
	private static Logger logger = LoggerFactory.getLogger(ValueSet.class);

	private final Map<MemoryRegion, StridedInterval> valueSet;
	private final DataWidth dataWidth;

	private ValueSet(DataWidth dataWidth)
	{
		this.valueSet = new HashMap<>();
		this.dataWidth = dataWidth;
	}

	public static ValueSet newTop(DataWidth width)
	{
		return new ValueSet(width);
	}

	public static ValueSet newGlobal(StridedInterval values)
	{
		return newGlobal(values.getDataWidth(), values);
	}

	public static ValueSet newGlobal(DataWidth width, StridedInterval values)
	{
		ValueSet valueSet = new ValueSet(width);
		valueSet.setValueOfGlobalRegion(values);
		return valueSet;
	}

	public static ValueSet newSingle(DataWidth width, LocalRegion region, StridedInterval values)
	{
		ValueSet valueSet = new ValueSet(width);
		valueSet.setValueOfRegion(region, values);
		return valueSet;
	}

	static ValueSet copy(ValueSet valueSet)
	{
		if (valueSet.isTop())
		{
			return newTop(valueSet.dataWidth);
		} else
		{
			ValueSet vs = new ValueSet(valueSet.dataWidth);
			for (Map.Entry<MemoryRegion, StridedInterval> entry : valueSet.valueSet.entrySet())
			{
				vs.setValueOfRegion(entry.getKey(), entry.getValue());
			}
			return vs;
		}
	}

	private void setValueOfRegion(MemoryRegion region, StridedInterval value)
	{
		assert !isTop() : "Cannot set region of top element";
		if (!this.dataWidth.equals(value.getDataWidth()))
		{
			throw new IllegalArgumentException("Invalid data width");
		}
		valueSet.put(region, value);
	}

	private void setValueOfGlobalRegion(StridedInterval value)
	{
		setValueOfRegion(GlobalRegion.getGlobalRegion(), value);
	}

	private StridedInterval getValueOfRegion(MemoryRegion region)
	{
		if (isTop())
		{
			return StridedInterval.getTop(dataWidth);
		} else if (!valueSet.containsKey(region))
		{
			return StridedInterval.getTop(dataWidth);
		}
		return valueSet.get(region);
	}

	public StridedInterval getValueOfGlobalRegion()
	{
		return getValueOfRegion(GlobalRegion.getGlobalRegion());
	}

	private Set<MemoryRegion> getRegions()
	{
		return valueSet.keySet();
	}

	public ValueSet union(ValueSet valueSet)
	{
		if (this.isTop() || valueSet.isTop())
		{
			newTop(dataWidth);
		}
		ValueSet answer = new ValueSet(dataWidth);
		for (MemoryRegion region : this.getRegions())
		{
			answer.setValueOfRegion(region, this.getValueOfRegion(region).union(valueSet.getValueOfRegion(region)));
		}
		for (MemoryRegion region : valueSet.getRegions())
		{
			answer.setValueOfRegion(region, this.getValueOfRegion(region).union(valueSet.getValueOfRegion(region)));
		}
		return answer;
	}

	private boolean isSingle()
	{
		return getRegions().size() == 1 && getRegions().stream().allMatch(region -> region instanceof LocalRegion);
	}

	public boolean isGlobal()
	{
		return getRegions().size() == 1 && getRegions().stream().allMatch(region -> region instanceof GlobalRegion);
	}

	private boolean isTop()
	{
		return getRegions().size() == 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ValueSet))
		{
			return false;
		}

		ValueSet other = (ValueSet) o;
		return this.dataWidth == other.dataWidth && (this.isTop() && other.isTop() || this.valueSet
				.equals(other.valueSet));
	}

	@Override
	public int hashCode()
	{
		int result = valueSet.hashCode();
		result = 31 * result + dataWidth.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		if (isTop())
		{
			return "ValueSet[TOP, " + dataWidth + "]";
		} else
		{
			return "ValueSet[" + valueSet.toString() + ", " + dataWidth + "]";
		}
	}

	public ValueSet sub(ValueSet valueSet)
	{
		if (!isTop() && valueSet.isGlobal())
		{
			ValueSet answer = new ValueSet(dataWidth);
			StridedInterval values = valueSet.getValueOfGlobalRegion();
			for (Map.Entry<MemoryRegion, StridedInterval> entry : this.valueSet.entrySet())
			{
				answer.setValueOfRegion(entry.getKey(), entry.getValue().sub(values));
			}
			return answer;
		} else
		{
			return newTop(dataWidth);
		}
	}

	public ValueSet add(ValueSet valueSet)
	{
		if (!isTop() && valueSet.isGlobal())
		{
			ValueSet answer = new ValueSet(dataWidth);
			StridedInterval values = valueSet.getValueOfGlobalRegion();
			for (Map.Entry<MemoryRegion, StridedInterval> entry : this.valueSet.entrySet())
			{
				answer.setValueOfRegion(entry.getKey(), entry.getValue().add(values));
			}
			return answer;
		}
		if (isGlobal() && !valueSet.isTop())
		{
			ValueSet answer = new ValueSet(dataWidth);
			StridedInterval values = this.getValueOfGlobalRegion();
			for (Map.Entry<MemoryRegion, StridedInterval> entry : valueSet.valueSet.entrySet())
			{
				answer.setValueOfRegion(entry.getKey(), entry.getValue().add(values));
			}
			return answer;
		}
		return newTop(dataWidth);
	}

	public ValueSet and(ValueSet valueSet)
	{
		ValueSet answer = new ValueSet(dataWidth);
		if (this.isGlobal() && valueSet.isGlobal())
		{
			answer.setValueOfGlobalRegion(this.getValueOfGlobalRegion().and(valueSet.getValueOfGlobalRegion()));
			return answer;
		} else if (this.isGlobal())
		{
			StridedInterval si = getValueOfGlobalRegion();
			if (si.isZero())
			{
				answer.setValueOfGlobalRegion(si);
				return answer;
			} else if (si.isSingletonSet() && si.contains(-1))
			{
				answer.setValueOfGlobalRegion(valueSet.getValueOfGlobalRegion());
				return answer;
			}
		} else if (valueSet.isGlobal())
		{
			return valueSet.and(this);
		}
		return newTop(dataWidth);
	}

	public ValueSet or(ValueSet valueSet)
	{
		ValueSet answer = new ValueSet(dataWidth);
		if (this.isGlobal() && valueSet.isGlobal())
		{
			answer.setValueOfGlobalRegion(this.getValueOfGlobalRegion().and(valueSet.getValueOfGlobalRegion()));
			return answer;
		} else if (this.isGlobal())
		{
			StridedInterval si = getValueOfGlobalRegion();
			if (si.isZero())
			{
				answer.setValueOfGlobalRegion(valueSet.getValueOfGlobalRegion());
				return answer;
			} else if (si.isSingletonSet() && si.contains(-1))
			{
				answer.setValueOfGlobalRegion(si);
				return answer;
			}
		} else if (valueSet.isGlobal())
		{
			return valueSet.or(this);
		}
		return newTop(dataWidth);
	}

	public ValueSet xor(ValueSet valueSet)
	{

		ValueSet answer = new ValueSet(dataWidth);
		if (this.isGlobal() && valueSet.isGlobal())
		{
			answer.setValueOfGlobalRegion(this.getValueOfGlobalRegion().and(valueSet.getValueOfGlobalRegion()));
			return answer;
		} else if (this.isGlobal())
		{
			StridedInterval si = getValueOfGlobalRegion();
			if (si.isZero())
			{
				answer.setValueOfGlobalRegion(valueSet.getValueOfGlobalRegion());
				return answer;
			}
		} else if (valueSet.isGlobal())
		{
			return valueSet.xor(this);
		}
		return newTop(dataWidth);
	}

	public ValueSet shiftLeft(ValueSet valueSet)
	{
		logger.warn("Operation (shiftLeft) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet shiftRight(ValueSet valueSet)
	{
		logger.warn("Operation (shiftRight) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet rotateLeft(ValueSet valueSet)
	{
		logger.warn("Operation (rotateLeft) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet rotateRight(ValueSet valueSet)
	{
		logger.warn("Operation (rotateRight) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet mul(ValueSet valueSet)
	{
		logger.warn("Operation (mul) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet div(ValueSet valueSet)
	{
		logger.warn("Operation (div) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet mod(ValueSet valueSet)
	{
		logger.warn("Operation (mod) not yet implemented");
		return newTop(dataWidth);
	}

	public ValueSet not()
	{
		logger.warn("Operation (not) not yet implemented");
		return newTop(dataWidth);
	}

}