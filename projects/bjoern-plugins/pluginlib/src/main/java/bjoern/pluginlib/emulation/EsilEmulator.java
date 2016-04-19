package bjoern.pluginlib.emulation;

import java.util.function.Predicate;

import bjoern.pluginlib.structures.Instruction;

public class EsilEmulator {

	EmulatorState state;

	// TODO: This is platform dependent.

	private Predicate<Instruction> isNotFunction =
			p -> p.getEsilCode().startsWith("rip,8,rsp,-=,rsp,=[],");

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
		String esilSeq = createEsilSequence(instructions, isNotFunction);

	}

	private String createEsilSequence(Iterable<Instruction> instructions,
									  Predicate<Instruction> p) {

		StringBuilder builder = new StringBuilder();

		for(Instruction instr: instructions)
		{
			String esilCode = instr.getEsilCode();
			if(p.test(instr))
				builder.append(esilCode + ",");
		}
		String instructionSeq = builder.toString();
		instructionSeq = instructionSeq.substring(0, instructionSeq.length() -1);
		return instructionSeq;
	}

}
