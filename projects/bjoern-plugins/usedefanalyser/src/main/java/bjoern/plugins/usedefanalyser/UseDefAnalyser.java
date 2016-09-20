package bjoern.plugins.usedefanalyser;


import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.data.DataObjectObserver;
import bjoern.plugins.vsa.data.ObservableDataObject;
import bjoern.plugins.vsa.data.Register;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.Transformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.*;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseDefAnalyser
{

	private final Map<ESILKeyword, ESILCommand> commands;
	private Observer observer;

	public UseDefAnalyser()
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
		commands.put(ESILKeyword.SHIFT_LEFT_ASSIGN,
				new ShiftLeftAssignCommand());
		commands.put(ESILKeyword.SHIFT_RIGHT_ASSIGN,
				new ShiftRightAssignCommand());
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

	public void analyse(BasicBlock block, List<Aloc> alocs)
	{
		AbstractEnvironment env = loadMachineState(alocs);
		analyse(block, env);
	}


	public void analyse(BasicBlock block, AbstractEnvironment env)
	{
		for (Instruction instruction : block.orderedInstructions())
		{
			if (observer != null)
			{
				observer.instruction = instruction;
			}
			String esilCode = instruction.getEsilCode();
			Transformer transformer = new ESILTransformer(commands);
			try
			{
				env = transformer.transform(esilCode, env);
			} catch (ESILTransformationException e)
			{
				e.printStackTrace();
			}
		}
	}

	private AbstractEnvironment loadMachineState(List<Aloc> alocs)
	{
		AbstractEnvironment env = new AbstractEnvironment();
		this.observer = new Observer(alocs);
		for (Aloc aloc : alocs)
		{
			if (aloc.isRegister())
			{
				ObservableDataObject<ValueSet> register = new
						ObservableDataObject<>(
						new Register(aloc.getName(),
								ValueSet.newTop(DataWidth.R64)));
				register.addObserver(observer);
				env.setRegister(register);
			}

		}
		return env;
	}

	private static class Observer implements DataObjectObserver<ValueSet>
	{
		private final List<Aloc> alocs;
		public Instruction instruction;

		public Observer(List<Aloc> alocs)
		{
			this.alocs = alocs;
		}

		@Override
		public void updateRead(DataObject<ValueSet> dataObject)
		{
			Aloc aloc = getAlocForDataObject(dataObject);
			if (aloc != null && instruction != null)
			{
				for (Edge edge : instruction.getEdges(Direction.OUT, "READ"))
				{
					if (edge.getVertex(Direction.IN).equals(aloc))
					{
						// edge exists -> skip
						return;
					}
				}
				instruction.addEdge("READ", aloc);
			}
		}

		@Override
		public void updateWrite(DataObject<ValueSet> dataObject, ValueSet
				value)
		{
			Aloc aloc = getAlocForDataObject(dataObject);
			if (aloc != null && instruction != null)
			{
				for (Edge edge : instruction.getEdges(Direction.OUT, "WRITE"))
				{
					if (edge.getVertex(Direction.IN).equals(aloc))
					{
						// edge exists -> skip
						return;
					}
				}
				instruction.addEdge("WRITE", aloc);
			}
		}

		private Aloc getAlocForDataObject(DataObject<ValueSet> dataObject)
		{
			for (Aloc aloc : alocs)
			{
				if (aloc.getName().equals(dataObject.getIdentifier()))
				{
					return aloc;
				}
			}
			return null;
		}
	}

}
