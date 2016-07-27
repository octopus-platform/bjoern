package bjoern.r2interface.creators;

import bjoern.structures.interpretations.Instruction;
import org.json.JSONObject;

public class RadareInstructionCreator
{
	public static Instruction createFromJSON(JSONObject jsonObj)
	{
		Long address = jsonObj.getLong("offset");
		String representation = jsonObj.getString("opcode");
		String esilCode = jsonObj.getString("esil");
		String bytes = jsonObj.getString("bytes");
		String comment = JSONUtils.getStringFromObject(jsonObj, "comment");

		return new Instruction.Builder(address).withRepresentation(representation).withESILCode(esilCode)
				.withBytes(bytes).withComment(comment).build();
	}
}


