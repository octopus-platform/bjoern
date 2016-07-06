package bjoern.r2interface.creators;


import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Instruction;
import org.json.JSONArray;
import org.json.JSONObject;


public class RadareBasicBlockCreator
{

	public static BasicBlock createFromJSON(JSONObject block)
	{
		long addr = block.getLong("offset");
		BasicBlock node = new BasicBlock(addr);
		initInstructionsFromJSON(node, block);
		return node;
	}

	private static void initInstructionsFromJSON(BasicBlock node,
			JSONObject block)
	{
		JSONArray instructionsJSON = block.getJSONArray("ops");

		int numberOfInstructions = instructionsJSON.length();
		for (int i = 0; i < numberOfInstructions; i++)
		{
			JSONObject jsonInstr = instructionsJSON.getJSONObject(i);
			Instruction instr = RadareInstructionCreator
					.createFromJSON(jsonInstr);
			node.addInstruction(instr);
		}
	}

}
