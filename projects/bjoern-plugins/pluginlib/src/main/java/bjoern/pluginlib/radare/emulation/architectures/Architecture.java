package bjoern.pluginlib.radare.emulation.architectures;

import bjoern.pluginlib.structures.Instruction;

public abstract class Architecture {

	public abstract boolean isCall(Instruction instr);

}
