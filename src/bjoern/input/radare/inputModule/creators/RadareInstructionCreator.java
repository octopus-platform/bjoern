package bjoern.input.radare.inputModule.creators;

import org.json.JSONObject;

import bjoern.input.common.structures.interpretations.DisassemblyLine;
import bjoern.input.common.structures.interpretations.Instruction;
import bjoern.input.radare.inputModule.JSONUtils;


public class RadareInstructionCreator
{
	public static Instruction createFromJSON(JSONObject jsonObj)
	{
		Instruction retval = new Instruction();
		String stringRepr = JSONUtils.getStringFromObject(jsonObj, "opcode");
		String bytes = JSONUtils.getStringFromObject(jsonObj, "bytes");
		retval.setStringRepr(stringRepr);
		retval.setBytes(bytes);

		Long addr = JSONUtils.getLongFromObject(jsonObj, "offset");
		retval.setAddr(addr);

		return retval;
	}

	public static Instruction createFromDisassemblyLine(DisassemblyLine line)
	{
		Instruction retval = new Instruction();

		Long addr = line.getAddr();
		retval.setAddr(addr);
		retval.setStringRepr(line.getInstruction());
		// TODO: bytes missing

		return retval;
	}

}
