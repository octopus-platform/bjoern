package inputModules.radare.creators;

import nodeStore.NodeStore;

import org.json.JSONArray;
import org.json.JSONObject;

import structures.BasicBlock;
import structures.Instruction;

public class RadareBasicBlockCreator
{

	public static BasicBlock createFromJSON(JSONObject block)
	{

		BasicBlock node = new BasicBlock();
		initFromJSON(node, block);
		return node;
	}

	public static void initFromJSON(BasicBlock node, JSONObject block)
	{
		long addr = block.getLong("offset");
		node.setAddr(addr);

		initInstructionsFromJSON(node, block);
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
			NodeStore.addNode(instr);
			node.addInstruction(instr);
		}
	}

}
