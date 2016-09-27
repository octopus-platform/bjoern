package bjoern.pluginlib.radare.emulation.esil.memaccess;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bjoern.r2interface.architectures.UnknownArchitectureException;
import com.tinkerpop.blueprints.Vertex;

import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.radare.emulation.ESILEmulator;
import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.r2interface.Radare;
import bjoern.r2interface.architectures.Architecture;

public class ESILStackAccessEvaluator {

	ESILEmulator emulator;
	StackState stackState;
	boolean isEmpty = false;

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

	public ESILStackAccessEvaluator(Radare radare) throws IOException, UnknownArchitectureException
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
			isEmpty = true;
			return null;
		}

		emulator.emulateWithoutCalls(entryBlock.orderedInstructions());
		long basePtrValue = emulator.getBasePointerValue();
		long stackPtrValue = emulator.getStackPointerValue();
		return new StackState(basePtrValue, stackPtrValue);
	}


	public List<MemoryAccess> extractMemoryAccesses(Instruction instr) throws IOException
	{
		List<MemoryAccess> retList = new LinkedList<MemoryAccess>();
		String esilCode = instr.getEsilCode();

		if(isEmpty || isUnconsideredStackAccess(instr))
			return retList;

		ESILTokenStream stream = new ESILTokenStream(esilCode);

		int index;
		while((index = stream.skipUntilToken(MEM_ACCESS_TOKENS)) !=
				ESILTokenStream.TOKEN_NOT_FOUND)
		{
			String esilMemAccessExpr = ESILAccessParser.parse(stream, index);

			if(!isResolvableStackAccess(esilMemAccessExpr))
				continue;

			MemoryAccess access = createMemoryAccessFromESILExpr(esilMemAccessExpr, instr);
			retList.add(access);
		}
		return retList;
	}

	private boolean isUnconsideredStackAccess(Instruction instr)
	{
		String code = instr.getRepresentation();
		Architecture arch = emulator.getArchitecture();
		return (arch.isCall(code) || arch.isPush(code) || arch.isPop(code) || arch.isRet(code));
	}

	private boolean isResolvableStackAccess(String esilMemAccessExpr)
	{
		if(esilMemAccessExpr == null)
			return false;

		String bpName = emulator.getBasePointerRegisterName();
		String spName = emulator.getStackPointerRegisterName();

		if(!esilMemAccessExpr.contains(bpName) && !esilMemAccessExpr.contains(spName))
			return false;

		return true;
	}

	private MemoryAccess createMemoryAccessFromESILExpr(String esilMemAccessExpr, Instruction instr) throws IOException
	{
		MemoryAccess access = new MemoryAccess();

		emulator.setStackState(stackState.getBasePtrValue(), stackState.getStackPtrValue());
		String addr = emulator.runEsilCode(esilMemAccessExpr);

		if(addr.startsWith(emulator.getBasePointerRegisterName()))
			addr = String.format("%d", stackState.getBasePtrValue());
		else if((addr.startsWith(emulator.getStackPointerRegisterName())))
			addr = String.format("%d", stackState.getStackPtrValue());

		access.setEsilExpression(esilMemAccessExpr);
		access.setInstructionRepr((String)instr.getProperty("repr"));
		access.setCompleteEsilExpression(instr.getEsilCode());
		access.setAddress(addr);

		return access;
	}

}
