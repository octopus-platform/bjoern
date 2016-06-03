package bjoern.pluginlib.radare.emulation.esil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ESILParser {

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

	public List<MemoryAccess> extractMemoryAccesses(String esilCode)
	{
		ESILTokenStream stream = new ESILTokenStream(esilCode);

		List<MemoryAccess> retList = new LinkedList<MemoryAccess>();

		int index;
		while((index = stream.skipUntilToken(MEM_ACCESS_TOKENS)) !=
				ESILTokenStream.TOKEN_NOT_FOUND)
		{
			retList.add(createMemoryAccessAt(stream, index));
		}
		return retList;
	}

	private MemoryAccess createMemoryAccessAt(ESILTokenStream stream, int index)
	{
		MemoryAccess access = new MemoryAccess();

		String operation = stream.getTokenAt(index);
		if(POKE_TOKENS.contains(operation))
			return createPokeMemoryAccessAt(stream, index);
		else if(PEEK_TOKENS.contains(operation))
			return createPeekMemoryAccessAt(stream, index);

		return null;
	}

	private MemoryAccess createPokeMemoryAccessAt(ESILTokenStream stream, int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private MemoryAccess createPeekMemoryAccessAt(ESILTokenStream stream, int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
