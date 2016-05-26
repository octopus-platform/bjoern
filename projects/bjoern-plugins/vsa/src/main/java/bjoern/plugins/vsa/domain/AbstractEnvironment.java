package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractEnvironment
{
	private final Map<String, ValueSet> registers;
	private final Map<String, Bool3> flags;

	public AbstractEnvironment()
	{
		registers = new HashMap<String, ValueSet>();
		flags = new HashMap<String, Bool3>();
	}

	public AbstractEnvironment(AbstractEnvironment inEnv)
	{
		this();
		for (String register : inEnv.getRegisters())
		{
			registers.put(register, ValueSet.copy(inEnv.getValueSetOfRegister(register)));
		}
		for (String flag : inEnv.getFlags())
		{
			flags.put(flag, inEnv.getValueOfFlag(flag));
		}
	}

	public Bool3 getValueOfFlag(String flag)
	{
		if (!flags.containsKey(flag))
		{
			return Bool3.MAYBE;
		}
		return flags.get(flag);
	}

	public void setValueOfFlag(String flag, Bool3 value)
	{
		flags.put(flag, value);
	}

	public ValueSet getValueSetOfRegister(String register)
	{
		if (!registers.containsKey(register))
		{
			return ValueSet.newTop(DataWidth.R64);
		}
		return registers.get(register);
	}

	public void setValueSetOfRegister(String register, ValueSet value)
	{
		registers.put(register, value);
	}

	private Set<String> getFlags()
	{
		return flags.keySet();
	}

	private Set<String> getRegisters()
	{
		return registers.keySet();
	}

	public AbstractEnvironment union(AbstractEnvironment absEnv)
	{
		AbstractEnvironment answer = new AbstractEnvironment();
		for (String register : this.getRegisters())
		{
			answer.setValueSetOfRegister(register,
					getValueSetOfRegister(register).union(absEnv.getValueSetOfRegister(register)));
		}
		for (String register : absEnv.getRegisters())
		{
			answer.setValueSetOfRegister(register,
					getValueSetOfRegister(register).union(absEnv.getValueSetOfRegister(register)));
		}
		for (String flag : this.getFlags())
		{
			answer.setValueOfFlag(flag, getValueOfFlag(flag).join(absEnv.getValueOfFlag(flag)));
		}
		for (String flag : absEnv.getFlags())
		{
			answer.setValueOfFlag(flag, getValueOfFlag(flag).join(absEnv.getValueOfFlag(flag)));
		}
		return answer;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AbstractEnvironment))
		{
			return false;
		}
		AbstractEnvironment other = (AbstractEnvironment) o;
		return registers.equals(other.registers) && flags.equals(other.flags);
	}

	@Override
	public int hashCode()
	{
		return registers.hashCode();
	}

	@Override
	public String toString()
	{
		return "AbstractEnvironment[" + registers.toString() + ", " + flags.toString() + "]";
	}
}
