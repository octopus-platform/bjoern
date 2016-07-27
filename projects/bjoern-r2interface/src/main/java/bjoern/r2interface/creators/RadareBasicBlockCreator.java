package bjoern.r2interface.creators;


import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Instruction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class RadareBasicBlockCreator
{

	public static BasicBlock createFromJSON(JSONObject block)
	{
		long addr = block.getLong("offset");
		JSONArray instructionsJSON = block.getJSONArray("ops");
		BasicBlock node = new BasicBlock.Builder(addr).withInstructions(getInstructionsFromJSON(instructionsJSON))
				.build();
		return node;
	}

	private static List<Instruction> getInstructionsFromJSON(JSONArray instructionsJSON)
	{

		List<Instruction> instructions = new LinkedList<>();
		int numberOfInstructions = instructionsJSON.length();
		for (int i = 0; i < numberOfInstructions; i++)
		{
			JSONObject instructionJSON = instructionsJSON.getJSONObject(i);
			Instruction instruction = RadareInstructionCreator.createFromJSON(instructionJSON);
			instructions.add(instruction);
		}
		return instructions;
	}
}
