package bjoern.r2interface.creators;

import bjoern.structures.interpretations.Instruction;
import org.json.JSONObject;

public class RadareInstructionCreator
{
	public static Instruction createFromJSON(JSONObject jsonObj)
	{
		Instruction retval = new Instruction(JSONUtils.getLongFromObject(jsonObj, "offset"));
		String stringRepr = JSONUtils.getStringFromObject(jsonObj, "opcode");
		retval.setStringRepr(stringRepr);
		String bytes = JSONUtils.getStringFromObject(jsonObj, "bytes");
		retval.setBytes(bytes);
		String esilCode = JSONUtils.getStringFromObject(jsonObj, "esil");
		retval.setEsilCode(esilCode);
		if (jsonObj.has("comment"))
		{
			String comment = JSONUtils.getStringFromObject(jsonObj, "comment");
			retval.setComment(comment);
		}
		return retval;
	}

}
