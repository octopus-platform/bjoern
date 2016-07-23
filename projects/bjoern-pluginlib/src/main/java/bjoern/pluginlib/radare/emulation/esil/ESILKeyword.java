package bjoern.pluginlib.radare.emulation.esil;

import java.util.HashMap;
import java.util.Map;

public enum ESILKeyword
{
	ASSIGNMENT("=", 2),
	COMPARE("==", 2),
	SMALLER("<", 2),
	SMALLER_OR_EQUAL("<=", 2),
	BIGGER(">", 2),
	BIGGER_OR_EQUAL(">=", 2),
	SHIFT_LEFT("<<", 2),
	SHIFT_RIGHT(">>", 2),
	ROTATE_LEFT("<<<", 2),
	ROTATE_RIGHT(">>>", 2),
	AND("&", 2),
	OR("|", 2),
	XOR("^", 2),
	ADD("+", 2),
	SUB("-", 2),
	MUL("*", 2),
	DIV("/", 2),
	MOD("%", 2),
	NEG("!", 1),
	INC("++", 1),
	DEC("--", 1),
	ADD_ASSIGN("+=", 2),
	SUB_ASSIGN("-=", 2),
	MUL_ASSIGN("*=", 2),
	DIV_ASSIGN("/=", 2),
	MOD_ASSIGN("%=", 2),
	SHIFT_LEFT_ASSIGN("<<=", 2),
	SHIFT_RIGHT_ASSIGN(">>=", 2),
	AND_ASSIGN("&=", 2),
	OR_ASSIGN("|=", 2),
	XOR_ASSIGN("^=", 2),
	INC_ASSIGN("++=", 2),
	DEC_ASSIGN("--=", 2),
	NEG_ASSIGN("!=", 2),
	POKE("=[]", 2),
	POKE_AST("=[*]", 2),
	POKE1("=[1]", 2),
	POKE2("=[2]", 2),
	POKE4("=[4]", 2),
	POKE8("=[8]", 2),
	PEEK("[]", 1),
	PEEK_AST("[*]", 1),
	PEEK1("[1]", 1),
	PEEK2("[2]", 1),
	PEEK4("[4]", 1),
	PEEK8("[8]", 1),
	START_CONDITIONAL("?{", 1),
	END_CONDITIONAL("}", 1);

	public final String keyword;
	public final int numberOfArgs;

	ESILKeyword(String s, int nArgs)
	{
		keyword = s;
		this.numberOfArgs = nArgs;
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
