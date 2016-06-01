package bjoern.pluginlib.radare.emulation;

import java.io.IOException;
import java.util.function.Predicate;

import bjoern.pluginlib.structures.Instruction;
import bjoern.r2interface.Radare;
import bjoern.r2interface.architectures.Architecture;

public class EsilEmulator {

	Architecture architecture;
	Radare wrappedRadare;

	public EsilEmulator(Radare radare) throws IOException
	{
		wrappedRadare = radare;
		setArchitecture(radare.getArchitecture());

		reset();
	}

	public void reset()
	{

	}

	public void setArchitecture(Architecture architecture)
	{
		this.architecture = architecture;
	}

	public void emulateWithoutCalls(Iterable<Instruction> instructions) throws IOException
	{
		String esilSeq = createEsilSequenceWithoutCalls(instructions);
		wrappedRadare.resetEsilState();
		wrappedRadare.runEsilCode(esilSeq);
	}

	public long getStackPointerValue() throws IOException
	{
		String stackRegisterName = architecture.getStackRegisterName();
		return getRegisterValue(stackRegisterName);
	}

	public long getBasePointerValue() throws IOException
	{
		String baseRegisterName = architecture.getBaseRegisterName();
		return getRegisterValue(baseRegisterName);
	}

	public long getRegisterValue(String registerName) throws IOException
	{
		String registerValueStr = wrappedRadare.getRegisterValue(registerName);
		if(registerValueStr == null)
			return 0;

		registerValueStr = registerValueStr.substring(2, registerValueStr.length() -1);
		return Long.parseUnsignedLong(registerValueStr, 16);
	}

	private String createEsilSequenceWithoutCalls(Iterable<Instruction> instructions)
	{
		Predicate<Instruction> isNotCall = p -> !architecture.isCall(p.getEsilCode());
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
