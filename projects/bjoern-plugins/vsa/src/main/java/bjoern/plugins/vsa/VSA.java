package bjoern.plugins.vsa;

import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.data.Flag;
import bjoern.plugins.vsa.data.Register;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.Transformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class VSA
{
	private Logger logger = LoggerFactory.getLogger(VSA.class);
	private final Map<Instruction, AbstractEnvironment> assignment;

	private static final Map<ESILKeyword, ESILCommand> commands;

	static
	{
		commands = new HashMap<>();
		commands.put(ESILKeyword.ASSIGNMENT, new AssignmentCommand());
		ESILCommand relationalCommand = new RelationalCommand();
		commands.put(ESILKeyword.ASSIGNMENT, new AssignmentCommand());
		commands.put(ESILKeyword.COMPARE, relationalCommand);
		commands.put(ESILKeyword.SMALLER, relationalCommand);
		commands.put(ESILKeyword.SMALLER_OR_EQUAL, relationalCommand);
		commands.put(ESILKeyword.BIGGER, relationalCommand);
		commands.put(ESILKeyword.BIGGER_OR_EQUAL, relationalCommand);
		commands.put(ESILKeyword.SHIFT_LEFT, new ShiftLeftCommand());
		commands.put(ESILKeyword.SHIFT_RIGHT, new ShiftRightCommand());
		commands.put(ESILKeyword.ROTATE_LEFT, new RotateLeftCommand());
		commands.put(ESILKeyword.ROTATE_RIGHT, new RotateRightCommand());
		commands.put(ESILKeyword.AND, new AndCommand());
		commands.put(ESILKeyword.OR, new OrCommand());
		commands.put(ESILKeyword.XOR, new XorCommand());
		commands.put(ESILKeyword.ADD, new AddCommand());
		commands.put(ESILKeyword.SUB, new SubCommand());
		commands.put(ESILKeyword.MUL, new MulCommand());
		commands.put(ESILKeyword.DIV, new DivCommand());
		commands.put(ESILKeyword.MOD, new ModCommand());
		commands.put(ESILKeyword.NEG, new NegateCommand());
		commands.put(ESILKeyword.INC, new IncCommand());
		commands.put(ESILKeyword.DEC, new DecCommand());
		commands.put(ESILKeyword.ADD_ASSIGN, new AddAssignCommand());
		commands.put(ESILKeyword.SUB_ASSIGN, new SubAssignCommand());
		commands.put(ESILKeyword.MUL_ASSIGN, new MulAssignCommand());
		commands.put(ESILKeyword.DIV_ASSIGN, new DivAssignCommand());
		commands.put(ESILKeyword.MOD_ASSIGN, new ModAssignCommand());
		commands.put(ESILKeyword.SHIFT_LEFT_ASSIGN, new ShiftLeftAssignCommand());
		commands.put(ESILKeyword.SHIFT_RIGHT_ASSIGN, new ShiftRightAssignCommand());
		commands.put(ESILKeyword.AND_ASSIGN, new AndAssignCommand());
		commands.put(ESILKeyword.OR_ASSIGN, new OrAssignCommand());
		commands.put(ESILKeyword.XOR_ASSIGN, new XorAssignCommand());
		commands.put(ESILKeyword.INC_ASSIGN, new IncAssignCommand());
		commands.put(ESILKeyword.DEC_ASSIGN, new DecAssignCommand());
		commands.put(ESILKeyword.NEG_ASSIGN, new NegAssignCommand());
		ESILCommand pokeCommand = new PokeCommand();
		commands.put(ESILKeyword.POKE, pokeCommand);
		commands.put(ESILKeyword.POKE_AST, pokeCommand);
		commands.put(ESILKeyword.POKE1, pokeCommand);
		commands.put(ESILKeyword.POKE2, pokeCommand);
		commands.put(ESILKeyword.POKE4, pokeCommand);
		commands.put(ESILKeyword.POKE8, pokeCommand);
		ESILCommand peekCommand = new PeekCommand();
		commands.put(ESILKeyword.PEEK, peekCommand);
		commands.put(ESILKeyword.PEEK_AST, peekCommand);
		commands.put(ESILKeyword.PEEK1, peekCommand);
		commands.put(ESILKeyword.PEEK2, peekCommand);
		commands.put(ESILKeyword.PEEK4, peekCommand);
		commands.put(ESILKeyword.PEEK8, peekCommand);
	}

	public VSA()
	{
		this.assignment = new HashMap<>();
	}

	public void performIntraProceduralVSA(Function function)
	{
		Queue<Instruction> worklist = new LinkedList<>();
		Transformer transformer = new ESILTransformer(VSA.commands);

		Instruction entry = Traversals.functionToEntryInstruction(function);
		if (entry == null)
		{
			return;
		}
		setAbstractEnvironment(entry, createInitialAbstractEnvironment(function));
		worklist.add(entry);
		while (!worklist.isEmpty())
		{
			AbstractEnvironment out;
			Instruction n = worklist.remove();
			try
			{
				out = transformer.transform(n.getEsilCode(), getAbstractEnvironment(n));
			} catch (ESILTransformationException e)
			{
				logger.error(e.getMessage());
				out = new AbstractEnvironment();
			} catch (NoSuchElementException e)
			{
				logger.error("Invalid esil stack");
				out = new AbstractEnvironment();
			}
			List<Instruction> successors = Traversals.instructionToSuccessors(n);
			for (Instruction successor : successors)
			{
//				if (getCounter(n) < getCounter(successor))
//				{
//					performWidening(out, getAbstractEnvironment(successor));
//				}
				if (updateAbstractEnvironment(successor, out))
				{
					worklist.add(successor);
				}
			}
//			incrementCounter(n);
		}

//		writeResults();
	}

	private AbstractEnvironment createInitialAbstractEnvironment(Function function)
	{
		AbstractEnvironment env = new AbstractEnvironment();
		for (Aloc aloc : Traversals.functionToAlocs(function))
		{
			if (aloc.isFlag())
			{
				env.setFlag(new Flag(aloc.getName(), Bool3.MAYBE));
			} else if (aloc.isRegister())
			{
				// TODO: Read initial values and the data width from aloc node.
				ValueSet valueSet;
				if (aloc.getName().equals("rsp"))
				{
					valueSet = ValueSet.newSingle(StridedInterval.getSingletonSet(0, DataWidth.R64));
				} else
				{
					valueSet = ValueSet.newTop(DataWidth.R64);
				}
				env.setRegister(new Register(aloc.getName(), valueSet));
			} else if (aloc.isLocalVariable())
			{
//				ValueSet valueSet = ValueSet.newTop(DataWidth.R64);
//				env.setLocalAloc(new LocalAloc(aloc.getName(), valueSet));
			}
		}
		return env;
	}

//	private void writeResults()
//	{
//		for (Instruction instr : assignment.keySet())
//		{
//			logger.info(instr.getEsilCode());
//			logger.info(assignment.get(instr).toString());
//			for (Edge edge : instr.getEdges(Direction.OUT, EdgeTypes.READ))
//			{
//				Aloc aloc = new Aloc(edge.getVertex(Direction.IN));
//				if (aloc.isFlag())
//				{
//					edge.setProperty(BjoernEdgeProperties.VALUE,
//							assignment.get(instr).getFlag(aloc.getName()).getBooleanValue().toString());
//				} else
//				{
//					edge.setProperty(BjoernEdgeProperties.VALUE,
//							assignment.get(instr).getRegister(aloc.getName()).getValue().toString());
//				}
//			}
//
//		}
//
//	}

	private boolean updateAbstractEnvironment(Instruction n, AbstractEnvironment amc)
	{
		AbstractEnvironment oldEnv = getAbstractEnvironment(n);
		if (oldEnv == null)
		{
			setAbstractEnvironment(n, amc);
			return true;
		} else
		{
			AbstractEnvironment newEnv;
			newEnv = oldEnv.union(amc);
			if (oldEnv.equals(newEnv))
			{
				return false;
			} else
			{
				setAbstractEnvironment(n, newEnv);
				return true;
			}
		}
	}

//	private int getCounter(Instruction n)
//	{
//		if (mycounter.containsKey(n))
//		{
//			return mycounter.get(n);
//		} else
//		{
//			return 0;
//		}
//	}
//
//	private void incrementCounter(Instruction n)
//	{
//		mycounter.put(n, getCounter(n) + 1);
//	}
//
//	private void performWidening(AbstractEnvironment newEnv, AbstractEnvironment oldEnv)
//	{
//		logger.info("Performing widening: " + oldEnv + " [<=>] " + newEnv);
//		for (Register register : newEnv.getRegisters())
//		{
//			String identifier = register.getIdentifier();
//			ValueSet valueSet = oldEnv.getRegister(identifier).getValue().widen(register.getValue());
//			newEnv.setRegister(new Register(identifier, valueSet));
//		}
//	}

	private AbstractEnvironment getAbstractEnvironment(Instruction n)
	{
		return assignment.get(n);
	}

	private void setAbstractEnvironment(Instruction n, AbstractEnvironment env)
	{
		assignment.put(n, env);
	}

}
