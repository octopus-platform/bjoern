package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.data.Flag;
import bjoern.plugins.vsa.data.Register;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An abstract environment represents a set of concrete states that can arise at a program point
 * (see Balakrishnan, Gogul, and Thomas Reps. "WYSINWYX: What you see is not what you eXecute.").
 */
public class AbstractEnvironment
{
	private final Map<String, DataObject<ValueSet>> registers;
	private final Map<String, DataObject<Bool3>> flags;

	public AbstractEnvironment()
	{
		registers = new HashMap<>();
		flags = new HashMap<>();
	}

	public AbstractEnvironment(AbstractEnvironment inEnv)
	{
		this();
		for (DataObject<ValueSet> register : inEnv.registers.values())
		{
			setRegister(register.copy());
		}
		for (DataObject<Bool3> flag : inEnv.flags.values())
		{
			setFlag(flag.copy());
		}
	}

	public void setFlag(DataObject<Bool3> flag)
	{
		this.flags.put(flag.getIdentifier(), flag);
	}

	public void setRegister(DataObject<ValueSet> register)
	{
		this.registers.put(register.getIdentifier(), register);
	}

	public DataObject<ValueSet> getRegister(String registerName)
	{
		if (!registers.containsKey(registerName))
		{
			this.registers.put(registerName, new Register(registerName, ValueSet.newTop(DataWidth.R64)));
		}
		DataObject<ValueSet> register = registers.get(registerName);
		return register;
	}

	public DataObject<Bool3> getFlag(String flagName)
	{
		if (!flags.containsKey(flagName))
		{
			this.flags.put(flagName, new Flag(flagName, Bool3.MAYBE));
		}
		DataObject<Bool3> flag = flags.get(flagName);
		return flag;
	}

	public AbstractEnvironment union(AbstractEnvironment absEnv)
	{
		AbstractEnvironment answer = new AbstractEnvironment();

		Set<String> registerIds = new HashSet<>();
		registerIds.addAll(registers.keySet());
		registerIds.addAll(absEnv.registers.keySet());
		for (String identifier : registerIds)
		{
			ValueSet value1 = this.getRegister(identifier).read();
			ValueSet value2 = absEnv.getRegister(identifier).read();
			answer.setRegister(new Register(identifier, value1.union(value2)));
		}

		Set<String> flagIds = new HashSet<>();
		flagIds.addAll(flags.keySet());
		flagIds.addAll(absEnv.flags.keySet());
		for (String identifier : flagIds)
		{
			Bool3 value1 = this.getFlag(identifier).read();
			Bool3 value2 = absEnv.getFlag(identifier).read();
			answer.setFlag(new Flag(identifier, value1.join(value2)));
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
		int result = 17;
		result = 31 * result + registers.hashCode();
		result = 31 * result + flags.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "AbstractEnvironment[" + registers.values().toString() + ", " + flags.values().toString() + "]";
	}

}
