package bjoern.pluginlib.radare.emulation;

import java.util.function.Predicate;

import bjoern.pluginlib.radare.emulation.architectures.Architecture;
import bjoern.pluginlib.structures.Instruction;

public class EsilEmulator {

	EmulatorState state;
	Architecture architecture;

	public EsilEmulator()
	{
		reset();
	}

	public void reset()
	{
		state = new EmulatorState();
	}

	public void setArchitecture(Architecture architecture)
	{
		this.architecture = architecture;
	}

	public void emulateWithoutCalls(Iterable<Instruction> instructions)
	{
		String esilSeq = createEsilSequenceWithoutCalls(instructions);

	}

	private String createEsilSequenceWithoutCalls(Iterable<Instruction> instructions)
	{

		Predicate<Instruction> isNotCall = p -> !architecture.isCall(p);
		return createEsilSequence(instructions, isNotCall);
	}

	/**
	 * Create a flat string of ESIL instructions from an iterable
	 * of Instructions, where an instruction is only included if
	 * it matches p.
	 */

	private String createEsilSequence(Iterable<Instruction> instructions,
												  Predicate<Instruction> p)
	{
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
