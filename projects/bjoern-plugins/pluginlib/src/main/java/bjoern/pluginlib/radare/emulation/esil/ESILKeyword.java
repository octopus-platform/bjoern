package bjoern.pluginlib.radare.emulation.esil;

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


	public final String keyword;

	ESILKeyword(String s)
	{
		keyword = s;
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
