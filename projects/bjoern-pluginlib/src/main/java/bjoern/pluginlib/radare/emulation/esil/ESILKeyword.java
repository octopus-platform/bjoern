package bjoern.pluginlib.radare.emulation.esil;

public enum ESILKeyword
{
	ASSIGNMENT("=", 2, true),
	COMPARE("==", 2, true),
	SMALLER("<", 2, true),
	SMALLER_OR_EQUAL("<=", 2, true),
	BIGGER(">", 2, true),
	BIGGER_OR_EQUAL(">=", 2, true),
	SHIFT_LEFT("<<", 2, false),
	SHIFT_RIGHT(">>", 2, false),
	ROTATE_LEFT("<<<", 2, false),
	ROTATE_RIGHT(">>>", 2, false),
	AND("&", 2, false),
	OR("|", 2, false),
	XOR("^", 2, false),
	ADD("+", 2, false),
	SUB("-", 2, false),
	MUL("*", 2, false),
	DIV("/", 2, false),
	MOD("%", 2, false),
	NEG("!", 1, false),
	INC("++", 1, false),
	DEC("--", 1, false),
	ADD_ASSIGN("+=", 2, true),
	SUB_ASSIGN("-=", 2, true),
	MUL_ASSIGN("*=", 2, true),
	DIV_ASSIGN("/=", 2, true),
	MOD_ASSIGN("%=", 2, true),
	SHIFT_LEFT_ASSIGN("<<=", 2, true),
	SHIFT_RIGHT_ASSIGN(">>=", 2, true),
	AND_ASSIGN("&=", 2, true),
	OR_ASSIGN("|=", 2, true),
	XOR_ASSIGN("^=", 2, true),
	INC_ASSIGN("++=", 2, true),
	DEC_ASSIGN("--=", 2, true),
	NEG_ASSIGN("!=", 2, true),
	POKE("=[]", 2, true),
	POKE_AST("=[*]", 2, true),
	POKE1("=[1]", 2, true),
	POKE2("=[2]", 2, true),
	POKE4("=[4]", 2, true),
	POKE8("=[8]", 2, true),
	PEEK("[]", 1, false),
	PEEK_AST("[*]", 1, false),
	PEEK1("[1]", 1, false),
	PEEK2("[2]", 1, false),
	PEEK4("[4]", 1, false),
	PEEK8("[8]", 1, false),
	START_CONDITIONAL("?{", 1, true),
	END_CONDITIONAL("}", 1, false);

	public final String keyword;
	public final int numberOfArgs;
	public final boolean sideEffect;

	ESILKeyword(String s, int nArgs, boolean sideEffect)
	{
		this.keyword = s;
		this.numberOfArgs = nArgs;
		this.sideEffect = sideEffect;
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
