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
		String esilCode = JSONUtils.getStringFromObject(jsonObj, "esil");
		String comment = JSONUtils.getStringFromObject(jsonObj, "comment");
		retval.setStringRepr(stringRepr);
		retval.setBytes(bytes);
		retval.setEsilCode(esilCode);
		retval.setComment(comment);
		return retval;
	}


}
