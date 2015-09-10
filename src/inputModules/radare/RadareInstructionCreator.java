package inputModules.radare;

import org.json.JSONObject;

import structures.Instruction;

public class RadareInstructionCreator
{
	public static Instruction createFromJSON(JSONObject jsonObj)
	{
		Instruction retval = new Instruction();
		String stringRepr = JSONUtils.getStringFromObject(jsonObj, "opcode");
		retval.setStringRepr(stringRepr);

		Long addr = JSONUtils.getLongFromObject(jsonObj, "offset");
		retval.setAddr(addr);

		return retval;
	}
}
