package bjoern.pluginlib.radare.emulation.architectures;

import bjoern.pluginlib.structures.Instruction;

public class X64Architecture extends Architecture {

	@Override
	public boolean isCall(Instruction instr)
	{
		String esilCode = instr.getEsilCode();
		return esilCode.startsWith("rip,8,rsp,-=,rsp,=[],");
	}

}
