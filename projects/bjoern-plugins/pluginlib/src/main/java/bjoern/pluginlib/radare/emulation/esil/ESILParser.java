package bjoern.pluginlib.radare.emulation.esil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ESILParser {

	private Set<String> MEM_ACCESS_TOKENS =
			new HashSet<String>(Arrays.asList(

					ESILKeyword.POKE.keyword, ESILKeyword.POKE1.keyword,
					ESILKeyword.POKE2.keyword, ESILKeyword.POKE4.keyword,
					ESILKeyword.POKE8.keyword, ESILKeyword.POKE_AST.keyword,

					ESILKeyword.PEEK.keyword, ESILKeyword.PEEK1.keyword,
					ESILKeyword.PEEK2.keyword, ESILKeyword.PEEK4.keyword,
					ESILKeyword.PEEK8.keyword, ESILKeyword.PEEK_AST.keyword

					));

	public List<MemoryAccess> extractMemoryAccesses(String esilCode)
	{
		ESILTokenStream stream = new ESILTokenStream(esilCode);

		List<MemoryAccess> retList = new LinkedList<MemoryAccess>();


		stream.skipUntilToken(MEM_ACCESS_TOKENS);


		return null;
	}

}
