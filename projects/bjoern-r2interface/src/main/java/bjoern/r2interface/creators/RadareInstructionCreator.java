package bjoern.r2interface.creators;

import bjoern.structures.interpretations.DisassemblyLine;
import bjoern.structures.interpretations.Instruction;
import org.json.JSONObject;

public class RadareInstructionCreator
{
	public static Instruction createFromJSON(JSONObject jsonObj)
	{
		Instruction retval = new Instruction(JSONUtils.getLongFromObject(jsonObj, "offset"));
		String stringRepr = JSONUtils.getStringFromObject(jsonObj, "opcode");
		String bytes = JSONUtils.getStringFromObject(jsonObj, "bytes");
		retval.setStringRepr(stringRepr);
		retval.setBytes(bytes);
		return retval;
	}

	public static Instruction createFromDisassemblyLine(DisassemblyLine line)
	{
		Instruction retval = new Instruction(line.getAddr());
		retval.setStringRepr(line.getInstruction());
		retval.setComment(line.getComment());
		// TODO: bytes missing

		return retval;
	}

}
