package bjoern.pluginlib.radare.emulation.esil;

import java.util.HashMap;
import java.util.Map;

public enum ESILKeyword
{
	ASSIGNMENT("="),
	COMPARE("=="),
	SMALLER("<"),
	SMALLER_OR_EQUAL("<="),
	BIGGER(">"),
	BIGGER_OR_EQUAL(">="),
	SHIFT_LEFT("<<"),
	SHIFT_RIGHT(">>"),
	ROTATE_LEFT("<<<"),
	ROTATE_RIGHT(">>>"),
	AND("&"),
	OR("|"),
	XOR("^"),
	ADD("+"),
	SUB("-"),
	MUL("*"),
	DIV("/"),
	MOD("%"),
	NEG("!"),
	INC("++"),
	DEC("--"),
	ADD_ASSIGN("+="),
	SUB_ASSIGN("-="),
	MUL_ASSIGN("*="),
	DIV_ASSIGN("/="),
	MOD_ASSIGN("%="),
	SHIFT_LEFT_ASSIGN("<<="),
	SHIFT_RIGHT_ASSIGN(">>="),
	AND_ASSIGN("&="),
	OR_ASSIGN("|="),
	XOR_ASSIGN("^="),
	INC_ASSIGN("++="),
	DEC_ASSIGN("--="),
	NEG_ASSIGN("!="),
	POKE("=[]"),
	POKE_AST("=[*]"),
	POKE1("=[1]"),
	POKE2("=[2]"),
	POKE4("=[4]"),
	POKE8("=[8]"),
	PEEK("[]"),
	PEEK_AST("[*]"),
	PEEK1("[1]"),
	PEEK2("[2]"),
	PEEK4("[4]"),
	PEEK8("[8]"),
	START_CONDITIONAL("?{"),
	END_CONDITIONAL("}");

	private static Map<ESILKeyword, Integer> keywordToNargs = new HashMap<ESILKeyword, Integer>();

	public final String keyword;

	ESILKeyword(String s)
	{
		keyword = s;
	}

	static{
		initializeKeywordToNargs();
	}

	private static void initializeKeywordToNargs()
	{
		keywordToNargs.put(ASSIGNMENT, 2);
		keywordToNargs.put(COMPARE, 2);

		keywordToNargs.put(SMALLER, 2);
		keywordToNargs.put(SMALLER_OR_EQUAL, 2);
		keywordToNargs.put(BIGGER, 2);
		keywordToNargs.put(BIGGER_OR_EQUAL, 2);

		keywordToNargs.put(SHIFT_LEFT, 2);

		keywordToNargs.put(SHIFT_RIGHT, 2);

		keywordToNargs.put(ROTATE_LEFT, 2);
		keywordToNargs.put(ROTATE_RIGHT, 2);

		keywordToNargs.put(AND, 2);
		keywordToNargs.put(OR, 2);
		keywordToNargs.put(XOR, 2);

		keywordToNargs.put(ADD, 2);
		keywordToNargs.put(SUB, 2);
		keywordToNargs.put(MUL, 2);
		keywordToNargs.put(DIV, 2);
		keywordToNargs.put(MOD, 2);

		keywordToNargs.put(NEG, 1);
		keywordToNargs.put(INC, 1);
		keywordToNargs.put(DEC, 1);

		keywordToNargs.put(ADD_ASSIGN, 2);
		keywordToNargs.put(SUB_ASSIGN, 2);
		keywordToNargs.put(MUL_ASSIGN, 2);
		keywordToNargs.put(DIV_ASSIGN, 2);
		keywordToNargs.put(MOD_ASSIGN, 2);

		keywordToNargs.put(SHIFT_LEFT_ASSIGN, 2);
		keywordToNargs.put(SHIFT_RIGHT_ASSIGN, 2);
		keywordToNargs.put(AND_ASSIGN, 2);
		keywordToNargs.put(OR_ASSIGN, 2);

		keywordToNargs.put(XOR_ASSIGN, 2);
		keywordToNargs.put(INC_ASSIGN, 2);
		keywordToNargs.put(DEC_ASSIGN, 2);
		keywordToNargs.put(NEG_ASSIGN, 2);

		keywordToNargs.put(POKE, 2);
		keywordToNargs.put(POKE_AST, 2);
		keywordToNargs.put(POKE1, 2);
		keywordToNargs.put(POKE2, 2);
		keywordToNargs.put(POKE4, 2);
		keywordToNargs.put(POKE8, 2);

		keywordToNargs.put(PEEK, 1);
		keywordToNargs.put(PEEK_AST, 1);
		keywordToNargs.put(PEEK1, 1);
		keywordToNargs.put(PEEK2, 1);
		keywordToNargs.put(PEEK4, 1);
		keywordToNargs.put(PEEK8, 1);

		keywordToNargs.put(START_CONDITIONAL, 1);
		keywordToNargs.put(END_CONDITIONAL, 1);

	}

	public static int nargs(ESILKeyword keyword)
	{
		return keywordToNargs.get(keyword);
	}

	public static ESILKeyword fromString(String word)
	{
		for (ESILKeyword command : values())
		{
			if (command.keyword.equals(word))
			{
				return command;
			}
		}
		return null;
	}

}
