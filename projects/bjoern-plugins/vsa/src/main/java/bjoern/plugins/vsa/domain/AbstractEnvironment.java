package bjoern.plugins.vsa.domain;

import bjoern.plugins.vsa.structures.Bool3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An abstract environment represents a set of concrete states that can arise
 * at a program point (see Balakrishnan, Gogul, and Thomas Reps. "WYSINWYX:
 * What you see is not what you eXecute.").
 */
public class AbstractEnvironment {
	private final Map<Object, ValueSet> registers;
	private final Map<Object, Bool3> flags;
	private final Map<Long, ValueSet> localVariables;
	// TODO parameterize rbp
	private Object basePointer = "rbp";

	public AbstractEnvironment() {
		registers = new HashMap<>();
		flags = new HashMap<>();
		localVariables = new HashMap<>();
	}

	public AbstractEnvironment(AbstractEnvironment inEnv) {
		this();
		for (Map.Entry<Object, ValueSet> entry : inEnv.registers.entrySet()) {
			setRegister(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<Object, Bool3> entry : inEnv.flags.entrySet()) {
			setFlag(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<Long, ValueSet> entry : inEnv.localVariables.entrySet()) {
			setLocalVariable(entry.getKey(), entry.getValue());
		}
	}

	public void setFlag(Object id, Bool3 flag) {
		this.flags.put(id, flag);
	}

	public void setRegister(Object id, ValueSet register) {
		this.registers.put(id, register);
	}

	public void setLocalVariable(Long offset, ValueSet value) {
		this.localVariables.put(offset, value);
	}

	public ValueSet getRegister(Object id) {
		return registers.get(id);
	}

	public ValueSet getBasePointer() {
		return getRegister(basePointer);
	}

	public Bool3 getFlag(Object id) {
		return flags.get(id);
	}

	public ValueSet getLocalVariable(Long offset) {
		return localVariables.get(offset);
	}

	public Iterable<Map.Entry<Object, ValueSet>> getRegisters() {
		return registers.entrySet();
	}

	public AbstractEnvironment union(AbstractEnvironment absEnv) {
		AbstractEnvironment answer = new AbstractEnvironment();

		Set<Object> registerIds = new HashSet<>();
		registerIds.addAll(registers.keySet());
		registerIds.addAll(absEnv.registers.keySet());
		for (Object id : registerIds) {
			ValueSet value1 = this.getRegister(id);
			ValueSet value2 = absEnv.getRegister(id);
			if (value1 == null || value2 == null) {
				continue;
			}
			answer.setRegister(id, value1.union(value2));
		}

		Set<Object> flagIds = new HashSet<>();
		flagIds.addAll(flags.keySet());
		flagIds.addAll(absEnv.flags.keySet());
		for (Object id : flagIds) {
			Bool3 value1 = this.getFlag(id);
			Bool3 value2 = absEnv.getFlag(id);
			if (value1 == null || value2 == null) {
				continue;
			}
			answer.setFlag(id, value1.join(value2));
		}

		Set<Long> localVariableIds = new HashSet<>();
		localVariableIds.addAll(localVariables.keySet());
		localVariableIds.addAll(absEnv.localVariables.keySet());
		for (Long offset : localVariableIds) {
			ValueSet value1 = this.getLocalVariable(offset);
			ValueSet value2 = absEnv.getLocalVariable(offset);
			if (value1 == null || value2 == null) {
				continue;
			}
			answer.setLocalVariable(offset, value1.union(value2));
		}

		return answer;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractEnvironment)) {
			return false;
		}
		AbstractEnvironment other = (AbstractEnvironment) o;
		return registers.equals(other.registers)
				&& flags.equals(other.flags)
				&& localVariables.equals(other.localVariables);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + registers.hashCode();
		result = 31 * result + flags.hashCode();
		result = 31 * result + localVariables.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "AbstractEnvironment["
				+ registers.toString() + ", "
				+ flags.toString() + ", "
				+ localVariables.toString() + "]";
	}

}
