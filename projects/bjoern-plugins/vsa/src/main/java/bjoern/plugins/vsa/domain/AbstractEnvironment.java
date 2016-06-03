package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.esil.stack.Flag;
import bjoern.plugins.vsa.transformer.esil.stack.Register;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractEnvironment
{
	private final Map<String, Register> registers;
	private final Map<String, Flag> flags;

	public AbstractEnvironment()
	{
		registers = new HashMap<>();
		flags = new HashMap<>();
	}

	public AbstractEnvironment(AbstractEnvironment inEnv)
	{
		this();
		for (Register register : inEnv.registers.values())
		{
			setRegister(new Register(register));
		}
		for (Flag flag : inEnv.flags.values())
		{
			setFlag(new Flag(flag));
		}
	}

	public Flag getFlag(String flag)
	{
		if (!flags.containsKey(flag))
		{
			return new Flag(flag, Bool3.MAYBE);
		}
		return flags.get(flag);
	}

	public Register getRegister(String register)
	{
		if (!registers.containsKey(register))
		{
			return new Register(register, ValueSet.newTop(DataWidth.R64));
		}
		return registers.get(register);
	}

	public Set<String> getFlags()
	{
		return flags.keySet();
	}

	public Set<String> getRegisters()
	{
		return registers.keySet();
	}

	public AbstractEnvironment union(AbstractEnvironment absEnv)
	{
		AbstractEnvironment answer = new AbstractEnvironment();
		for (Register register : registers.values())
		{
			answer.setRegister(new Register(register));
		}
		for (Register register : absEnv.registers.values())
		{
			answer.setRegister(new Register(register));
		}
		for (Flag flag : flags.values())
		{
			answer.setFlag(new Flag(flag));
		}
		for (Flag flag : absEnv.flags.values())
		{
			answer.setFlag(new Flag(flag));
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

	public void setFlag(Flag flag)
	{
		this.flags.put(flag.getIdentifier(), flag);
	}

	public void setRegister(Register register)
	{
		this.registers.put(register.getIdentifier(), register);
	}
}
