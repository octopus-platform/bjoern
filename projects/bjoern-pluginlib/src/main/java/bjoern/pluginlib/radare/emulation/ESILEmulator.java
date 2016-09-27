package bjoern.pluginlib.radare.emulation;

import bjoern.pluginlib.structures.Instruction;
import bjoern.r2interface.Radare;
import bjoern.r2interface.architectures.Architecture;
import bjoern.r2interface.architectures.UnknownArchitectureException;

import java.io.IOException;
import java.util.function.Predicate;

public class ESILEmulator {

	Architecture architecture;
	Radare radare;

	public ESILEmulator(Radare radare) throws IOException, UnknownArchitectureException
	{
		this.radare = radare;
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
		radare.resetEsilState();
		runEsilCode(esilSeq);
	}

	public void setStackState(long basePointer, long stackPointer) throws IOException
	{
		String bpName = getBasePointerRegisterName();
		String spName = getStackPointerRegisterName();

		runEsilCode(String.format("%d,%s,=", stackPointer, spName));
		runEsilCode(String.format("%d,%s,=", basePointer, bpName));
	}

	public String runEsilCode(String esilCode) throws IOException
	{
		return radare.runEsilCode(esilCode);
	}

	public long getStackPointerValue() throws IOException
	{
		String stackRegisterName = getStackPointerRegisterName();
		return getRegisterValue(stackRegisterName);
	}

	public long getBasePointerValue() throws IOException
	{
		String baseRegisterName = getBasePointerRegisterName();
		return getRegisterValue(baseRegisterName);
	}

	public long getRegisterValue(String registerName) throws IOException
	{
		String registerValueStr = radare.getRegisterValue(registerName);
		if(registerValueStr == null)
			return 0;

		registerValueStr = registerValueStr.substring(2, registerValueStr.length() -1);
		return Long.parseUnsignedLong(registerValueStr, 16);
	}

	public String getBasePointerRegisterName()
	{
		return architecture.getBaseRegisterName();
	}

	public String getStackPointerRegisterName()
	{
		return architecture.getStackRegisterName();
	}

	private String createEsilSequenceWithoutCalls(Iterable<Instruction> instructions)
	{
		Predicate<Instruction> isNotCall = p -> !architecture.isCall(p.getRepresentation());
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

	public Architecture getArchitecture()
	{
		return this.architecture;
	}


}
