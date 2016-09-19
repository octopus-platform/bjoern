package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;

public class ESILCommandFactory
{
	public static ESILCommand getCommand(ESILKeyword keyword)
	{
		switch (keyword)
		{
			case ASSIGNMENT:
				return new AssignmentCommand();
			case COMPARE:
			case SMALLER:
			case SMALLER_OR_EQUAL:
			case BIGGER:
			case BIGGER_OR_EQUAL:
				return new RelationalCommand();
			case SHIFT_LEFT:
				return new ShiftLeftCommand();
			case SHIFT_RIGHT:
				return new ShiftRightCommand();
			case ROTATE_LEFT:
				return new RotateLeftCommand();
			case ROTATE_RIGHT:
				return new RotateRightCommand();
			case AND:
				return new AndCommand();
			case OR:
				return new OrCommand();
			case XOR:
				return new XorCommand();
			case ADD:
				return new AddCommand();
			case SUB:
				return new SubCommand();
			case MUL:
				return new MulCommand();
			case DIV:
				return new DivCommand();
			case MOD:
				return new ModCommand();
			case NEG:
				return new NegateCommand();
			case INC:
				return new IncCommand();
			case DEC:
				return new DecCommand();
			case ADD_ASSIGN:
				return new AddAssignCommand();
			case SUB_ASSIGN:
				return new SubAssignCommand();
			case MUL_ASSIGN:
				return new MulAssignCommand();
			case DIV_ASSIGN:
				return new DivAssignCommand();
			case MOD_ASSIGN:
				return new ModAssignCommand();
			case SHIFT_LEFT_ASSIGN:
				return new ShiftLeftAssignCommand();
			case SHIFT_RIGHT_ASSIGN:
				return new ShiftRightAssignCommand();
			case AND_ASSIGN:
				return new AndAssignCommand();
			case OR_ASSIGN:
				return new OrAssignCommand();
			case XOR_ASSIGN:
				return new XorAssignCommand();
			case INC_ASSIGN:
				return new IncAssignCommand();
			case DEC_ASSIGN:
				return new DecAssignCommand();
			case NEG_ASSIGN:
				return new NegAssignCommand();
			case POKE:
			case POKE_AST:
			case POKE1:
			case POKE2:
			case POKE4:
			case POKE8:
				return new PokeCommand();
			case PEEK:
			case PEEK_AST:
			case PEEK1:
			case PEEK2:
			case PEEK4:
			case PEEK8:
				return new PeekCommand();
			default:
				throw new ESILTransformationException("Invalid keyword " + keyword);
		}
	}
}
