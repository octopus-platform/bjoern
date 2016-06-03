package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.esil.stack.Flag;
import bjoern.plugins.vsa.transformer.esil.stack.Register;

import java.util.*;

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

	public void setFlag(Flag flag)
	{
		this.flags.put(flag.getIdentifier(), flag);
	}

	public void setRegister(Register register)
	{
		this.registers.put(register.getIdentifier(), register);
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

	public Collection<Register> getRegisters()
	{
		return registers.values();
	}

	public Collection<Flag> getFlags()
	{
		return flags.values();
	}

	public AbstractEnvironment union(AbstractEnvironment absEnv)
	{
		AbstractEnvironment answer = new AbstractEnvironment();

		Set<String> registerIds = new HashSet<>();
		registerIds.addAll(registers.keySet());
		registerIds.addAll(absEnv.registers.keySet());
		for (String identifier : registerIds)
		{
			answer.setRegister(new Register(identifier,
					getRegister(identifier).getValue().union(absEnv.getRegister(identifier).getValue())));
		}

		Set<String> flagIds = new HashSet<>();
		flagIds.addAll(flags.keySet());
		flagIds.addAll(absEnv.flags.keySet());
		for (String identifier : flagIds)
		{
			answer.setFlag(new Flag(identifier,
					getFlag(identifier).getBooleanValue().join(absEnv.getFlag(identifier).getBooleanValue())));
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
		int result = registers.hashCode();
		result = 31 * result + flags.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "AbstractEnvironment[" + registers.values().toString() + ", " + flags.values().toString() + "]";
	}

}
