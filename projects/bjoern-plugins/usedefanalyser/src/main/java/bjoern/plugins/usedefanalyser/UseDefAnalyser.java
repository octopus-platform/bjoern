package bjoern.plugins.usedefanalyser;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.data.*;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.Transformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.*;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseDefAnalyser {
	private static final Logger logger = LoggerFactory
			.getLogger(UseDefAnalyser.class);

	private final Map<ESILKeyword, ESILCommand> commands;
	private Instruction instruction;
	private boolean ignoreAccesses;

	public UseDefAnalyser() {
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
		ESILCommand pokeCommand = stack ->
		{
			ignoreAccesses = true;
			ValueSet destinationAddress = stack.pop().execute(stack)
			                                   .getValue();
			ignoreAccesses = false;
			ValueSet value = stack.pop().execute(stack).getValue();
			return null;
		};
		commands.put(ESILKeyword.POKE, pokeCommand);
		commands.put(ESILKeyword.POKE_AST, pokeCommand);
		commands.put(ESILKeyword.POKE1, pokeCommand);
		commands.put(ESILKeyword.POKE2, pokeCommand);
		commands.put(ESILKeyword.POKE4, pokeCommand);
		commands.put(ESILKeyword.POKE8, pokeCommand);
		ESILCommand peekCommand = stack ->
		{
			ignoreAccesses = true;
			ValueSet address = stack.pop().execute(stack).getValue();
			ignoreAccesses = false;
			return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
		};
		commands.put(ESILKeyword.PEEK, peekCommand);
		commands.put(ESILKeyword.PEEK_AST, peekCommand);
		commands.put(ESILKeyword.PEEK1, peekCommand);
		commands.put(ESILKeyword.PEEK2, peekCommand);
		commands.put(ESILKeyword.PEEK4, peekCommand);
		commands.put(ESILKeyword.PEEK8, peekCommand);
		ignoreAccesses = false;
	}

	public void analyse(BasicBlock block, List<Aloc> alocs) {
		AbstractEnvironment env = loadMachineState(alocs);
		analyse(block, env);
	}


	public void analyse(BasicBlock block, AbstractEnvironment env) {
		for (Instruction instruction : block.orderedInstructions()) {
			analyse(instruction, env);

		}
	}

	public void analyse(Instruction instruction, AbstractEnvironment env) {
		this.instruction = instruction;
		String esilCode = instruction.getEsilCode();
		analyse(esilCode, env);
	}

	public void analyse(String esilCode, AbstractEnvironment env) {
		Transformer transformer = new ESILTransformer(commands);
		try {
			transformer.transform(esilCode, env);
		} catch (ESILTransformationException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("Unknown error");
		}
	}

	private AbstractEnvironment loadMachineState(List<Aloc> alocs) {
		AbstractEnvironment env = new AbstractEnvironment();

		for (Aloc aloc : alocs) {
			if (aloc.isRegister()) {
				DataObject<ValueSet> register = new Register(aloc.getName(),
						ValueSet.newTop(DataWidth.R64));
				ObservableDataObject<ValueSet> dataObject = new
						ObservableDataObject<>(register);
				dataObject.addObserver(
						new DataObjectAccessObserver<>(aloc));
				register = dataObject;
				env.setRegister(register);
			}
			if (aloc.isFlag()) {
				ObservableDataObject<Bool3> flag = new
						ObservableDataObject<>(
						new Flag(aloc.getName(), Bool3.MAYBE));
				flag.addObserver(new DataObjectAccessObserver<>(aloc));
				env.setFlag(flag);
			}

		}
		return env;
	}

	private class DataObjectAccessObserver<T>
			implements DataObjectObserver<T> {
		private final Aloc aloc;

		public DataObjectAccessObserver(Aloc aloc) {
			this.aloc = aloc;
		}

		@Override
		public void updateRead(DataObject<T> dataObject) {
			if (null == instruction || ignoreAccesses) {
				return;
			}
			for (Edge edge : instruction.getEdges(Direction.OUT, "READ")) {
				if (edge.getVertex(Direction.IN).equals(aloc)) {
					// edge exists -> skip
					return;
				}
			}
			// add read edge from instruction to aloc
			instruction.addEdge("READ", aloc);
		}

		@Override
		public void updateWrite(DataObject<T> dataObject, T value) {
			if (null == instruction) {
				return;
			}
			for (Edge edge : instruction.getEdges(Direction.OUT, "WRITE")) {
				if (edge.getVertex(Direction.IN).equals(aloc)) {
					// edge exists -> skip
					return;
				}
			}
			// add write edge from instruction to aloc
			instruction.addEdge("WRITE", aloc);
		}
	}

}
