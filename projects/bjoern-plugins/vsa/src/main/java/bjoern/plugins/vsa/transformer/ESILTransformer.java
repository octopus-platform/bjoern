package bjoern.plugins.vsa.transformer;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenEvaluator;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.Flag;
import bjoern.plugins.vsa.transformer.esil.stack.Register;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;

public class ESILTransformer implements Transformer
{
	private Logger logger = LoggerFactory.getLogger(ESILTransformer.class);
	private AbstractEnvironment outEnv;
	private Deque<ESILStackItem<ValueSet>> esilStack;

	private ESILTokenStream tokenStream;
	private ESILTokenEvaluator esilParser = new ESILTokenEvaluator();

	@Override
	public AbstractEnvironment transform(String esilCode, AbstractEnvironment env)
	{
		outEnv = new AbstractEnvironment(env);
		esilStack = new LinkedList<>();
		tokenStream = new ESILTokenStream(esilCode);

		logger.info("Transforming [" + esilCode + "]");

		if (esilCode.equals(""))
		{
			return outEnv;
		}

		while (tokenStream.hasNext())
		{
			String token = tokenStream.next();
			if (esilParser.isEsilKeyword(token))
			{
				executeEsilCommand(ESILKeyword.fromString(token));
			} else if (esilParser.isNumericConstant(token))
			{
				pushValueSet(ValueSet
						.newGlobal(StridedInterval
								.getSingletonSet(esilParser.parseNumericConstant(token), DataWidth.R64)));
			} else if (esilParser.isRegister(token))
			{
				esilStack.push(outEnv.getRegister(token));
			} else if (esilParser.isFlag(token))
			{
				esilStack.push(outEnv.getFlag(token));
			} else
			{
				throw new ESILTransformationException("Unknown ESIL token (" + token + ")");
			}
		}

		return outEnv;
	}

	private ValueSet popValueSet()
	{
		return esilStack.pop().getValue();
	}

	private void pushValueSet(ValueSet valueSet)
	{
		esilStack.push(new ValueSetContainer(valueSet));
	}

	private void executeEsilCommand(ESILKeyword command)
	{
		logger.debug("Executing esil command: " + command);
		logger.debug("Stack content: " + esilStack);
		logger.debug("Environment: " + outEnv);
		switch (command)
		{
			case ASSIGNMENT:
				executeAssignment();
				break;
			case COMPARE:
				logger.warn("Operation (compare) not yet implemented");
				esilStack.pop();
				esilStack.pop();
				break;
			case SMALLER:
			case SMALLER_OR_EQUAL:
			case BIGGER:
			case BIGGER_OR_EQUAL:
				logger.warn("Operation (smaller*, bigger*) not yet implemented");
				esilStack.pop();
				esilStack.pop();
				esilStack.push(
						new ValueSetContainer(ValueSet.newGlobal(StridedInterval.getInterval(0, 1, DataWidth.R1))));
				break;
			case SHIFT_LEFT:
				executeShiftLeft();
				break;
			case SHIFT_RIGHT:
				executeShiftRight();
				break;
			case ROTATE_LEFT:
				executeRotateLeft();
				break;
			case ROTATE_RIGHT:
				executeRotateRight();
				break;
			case AND:
				executeAnd();
				break;
			case OR:
				executeOr();
				break;
			case XOR:
				executeXor();
				break;
			case ADD:
				executeAdd();
				break;
			case SUB:
				executeSub();
				break;
			case MUL:
				executeMul();
				break;
			case DIV:
				executeDiv();
				break;
			case MOD:
				executeMod();
				break;
			case NEG:
				executeNeg();
				break;
			case INC:
				executeInc();
				break;
			case DEC:
				executeDec();
				break;
			case ADD_ASSIGN:
				executeAddAssign();
				break;
			case SUB_ASSIGN:
				executeSubAssign();
				break;
			case MUL_ASSIGN:
				executeMulAssign();
				break;
			case DIV_ASSIGN:
				executeDivAssign();
				break;
			case MOD_ASSIGN:
				executeModAssign();
				break;
			case SHIFT_LEFT_ASSIGN:
				executeShiftLeftAssign();
				break;
			case SHIFT_RIGHT_ASSIGN:
				executeShiftRightAssign();
				break;
			case AND_ASSIGN:
				executeAndAssign();
				break;
			case OR_ASSIGN:
				executeOrAssign();
				break;
			case XOR_ASSIGN:
				executeXorAssign();
				break;
			case INC_ASSIGN:
				executeIncAssign();
				break;
			case DEC_ASSIGN:
				executeDecAssign();
				break;
			case NEG_ASSIGN:
				executeNegAssign();
				break;
			case POKE:
			case POKE_AST:
			case POKE1:
			case POKE2:
			case POKE4:
			case POKE8:
				executePoke();
				break;
			case PEEK:
			case PEEK_AST:
			case PEEK1:
			case PEEK2:
			case PEEK4:
			case PEEK8:
				executePeek();
				break;
			case START_CONDITIONAL:
				executeConditional();
				break;
			case END_CONDITIONAL:
				break;
			default:
				break;
		}
	}

	private void executeMul()
	{
		pushValueSet(popValueSet().mul(popValueSet()));
	}

	private void executeMulAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeMul();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeDiv()
	{
		pushValueSet(popValueSet().div(popValueSet()));
	}

	private void executeDivAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeDiv();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeMod()
	{
		pushValueSet(popValueSet().mod(popValueSet()));
	}

	private void executeModAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeMod();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeShiftLeft()
	{
		pushValueSet(popValueSet().shiftLeft(popValueSet()));
	}

	private void executeShiftLeftAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeShiftLeft();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeShiftRight()
	{
		pushValueSet(popValueSet().shiftRight(popValueSet()));
	}

	private void executeShiftRightAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeShiftRight();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeRotateLeft()
	{
		pushValueSet(popValueSet().rotateLeft(popValueSet()));
	}

	private void executeRotateRight()
	{
		pushValueSet(popValueSet().rotateRight(popValueSet()));
	}

	private void executeNeg()
	{
		// TODO: not sure what neg (!) does!?
		pushValueSet(popValueSet().negate());
	}

	private void executeNegAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeNeg();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeXor()
	{
		pushValueSet(popValueSet().xor(popValueSet()));
	}


	private void executeXorAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeXor();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeOr()
	{
		pushValueSet(popValueSet().or(popValueSet()));
	}

	private void executeOrAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeOr();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeAnd()
	{
		pushValueSet(popValueSet().and(popValueSet()));
	}

	private void executeAndAssign()
	{
		ESILStackItem<ValueSet> element = esilStack.peek();
		executeAnd();
		esilStack.push(element);
		executeAssignment();
	}

	private void executeConditional()
	{
		ValueSet valueSet = popValueSet();
		if (valueSet.getValueOfGlobalRegion().isZero())
		{
			tokenStream.skipUntilToken(ESILKeyword.END_CONDITIONAL.keyword);
		} else if (!valueSet.getValueOfGlobalRegion().isOne())
		{
			// nothing to do
		} else
		{
			StringBuilder builder = new StringBuilder();
			do
			{
				builder.append(tokenStream.next()).append(",");
			} while (tokenStream.hasNext());

			builder.setLength(builder.length() - 1);
			String esilCode = builder.toString();

			AbstractEnvironment amc = new ESILTransformer().transform(esilCode, outEnv);
			if (esilCode.indexOf("}") == esilCode.length() - 1)
			{
				this.outEnv = amc.union(this.outEnv);
			} else
			{
				this.outEnv = amc
						.union(new ESILTransformer().transform(esilCode.substring(esilCode.indexOf("}") + 2), outEnv));
			}
		}
	}

	private void executePeek()
	{
		logger.info("Loading data from memory not yet supported");
		esilStack.pop();
		pushValueSet(ValueSet.newTop(DataWidth.R64));
	}

	private void executePoke()
	{
		logger.info("Writing data to memory not yet supported");
		esilStack.pop();
		esilStack.pop();
	}

	private void executeInc()
	{
		pushValueSet(ValueSet.newGlobal(StridedInterval.getSingletonSet(1, DataWidth.R64)));
		executeAdd();
	}

	private void executeIncAssign()
	{
		ESILStackItem<ValueSet> obj = esilStack.peek();
		executeInc();
		esilStack.push(obj);
		executeAssignment();
	}

	private void executeDec()
	{
		pushValueSet(ValueSet.newGlobal(StridedInterval.getSingletonSet(1, DataWidth.R64)));
		executeSub();
	}

	private void executeDecAssign()
	{
		ESILStackItem<ValueSet> obj = esilStack.peek();
		executeDec();
		esilStack.push(obj);
		executeAssignment();
	}

	private void executeAdd()
	{
		pushValueSet(popValueSet().add(popValueSet()));
	}

	private void executeAddAssign()
	{
		ESILStackItem<ValueSet> obj = esilStack.peek();
		executeAdd();
		esilStack.push(obj);
		executeAssignment();
	}

	private void executeSub()
	{
		pushValueSet(popValueSet().sub(popValueSet()));
	}

	private void executeSubAssign()
	{
		ESILStackItem<ValueSet> obj = esilStack.peek();
		executeSub();
		esilStack.push(obj);
		executeAssignment();
	}

	private void executeAssignment()
	{
		Object obj = esilStack.pop();
		if (obj instanceof Register)
		{
			String identifier = ((Register) obj).getIdentifier();
			outEnv.setRegister(new Register(identifier, popValueSet()));
		} else if (obj instanceof Flag)
		{
			String identifier = ((Flag) obj).getIdentifier();
			StridedInterval stridedInterval = popValueSet().getValueOfGlobalRegion();
			Flag flag;
			if (stridedInterval.isZero())
			{
				flag = new Flag(identifier, Bool3.FALSE);
			} else if (stridedInterval.isOne())
			{
				flag = new Flag(identifier, Bool3.TRUE);
			} else
			{
				flag = new Flag(identifier, Bool3.MAYBE);
			}
			outEnv.setFlag(flag);
		}
	}

}
