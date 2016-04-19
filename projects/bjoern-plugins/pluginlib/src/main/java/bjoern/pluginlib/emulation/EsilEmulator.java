package bjoern.pluginlib.emulation;

import bjoern.pluginlib.structures.Instruction;

public class EsilEmulator {

	EmulatorState state;

	public EsilEmulator()
	{
		reset();
	}

	public void reset()
	{
		state = new EmulatorState();
	}

	public void emulate(Iterable<Instruction> instructions)
	{
		for(Instruction instr: instructions)
		{

		}
	}

}
