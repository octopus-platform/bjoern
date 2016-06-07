package bjoern.pluginlib.radare.emulation.esil.memaccess;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tinkerpop.blueprints.Vertex;

import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.radare.emulation.ESILEmulator;
import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.r2interface.Radare;

public class ESILMemAccessEvaluator {

	ESILEmulator emulator;
	StackState stackState;

	private static final Set<String> POKE_TOKENS =
			new HashSet<String>(Arrays.asList(
					ESILKeyword.POKE.keyword, ESILKeyword.POKE1.keyword,
					ESILKeyword.POKE2.keyword, ESILKeyword.POKE4.keyword,
					ESILKeyword.POKE8.keyword, ESILKeyword.POKE_AST.keyword
			));


	private static final Set<String> PEEK_TOKENS =
			new HashSet<String>(Arrays.asList(

			ESILKeyword.PEEK.keyword, ESILKeyword.PEEK1.keyword,
			ESILKeyword.PEEK2.keyword, ESILKeyword.PEEK4.keyword,
			ESILKeyword.PEEK8.keyword, ESILKeyword.PEEK_AST.keyword));

	private static Set<String> MEM_ACCESS_TOKENS = new HashSet<String>();

	static
	{
		MEM_ACCESS_TOKENS.addAll(POKE_TOKENS);
		MEM_ACCESS_TOKENS.addAll(PEEK_TOKENS);
	}

	public ESILMemAccessEvaluator(Radare radare) throws IOException
	{
		emulator = new ESILEmulator(radare);
	}

	public void initializeForFunction(Vertex function) throws IOException
	{
		stackState = emulateFirstBasicBlock(function);
	}

	private StackState emulateFirstBasicBlock(Vertex function) throws IOException
	{
		BasicBlock entryBlock;
		try{
			entryBlock = Traversals.functionToEntryBlock(function);

		} catch(RuntimeException ex) {
			System.err.println("Warning: function without entry block");
			return null;
		}

		emulator.emulateWithoutCalls(entryBlock.getInstructions());
		long basePtrValue = emulator.getBasePointerValue();
		long stackPtrValue = emulator.getStackPointerValue();
		return new StackState(basePtrValue, stackPtrValue);
	}


	public List<String> extractMemoryAccesses(String esilCode)
	{
		ESILTokenStream stream = new ESILTokenStream(esilCode);

		List<String> retList = new LinkedList<String>();

		int index;
		while((index = stream.skipUntilToken(MEM_ACCESS_TOKENS)) !=
				ESILTokenStream.TOKEN_NOT_FOUND)
		{
			String bpName = emulator.getBasePointerRegisterName();

			String esilMemAccessCode = ESILAccessParser.parse(stream, index);
			if(esilMemAccessCode != null && esilCode.contains(bpName))
				retList.add(esilMemAccessCode);
		}
		return retList;
	}

}
