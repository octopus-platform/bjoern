package bjoern.plugins.usedefanalyser;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.data.DataObjectObserver;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.esil.commands.*;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UseDefAnalyser {
	private static final Logger logger = LoggerFactory
			.getLogger(UseDefAnalyser.class);

	private final Map<ESILKeyword, ESILCommand> commands;
	private Instruction instruction;
	private boolean ignoreAccesses;
	private Map<Object, Aloc> alocs;

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
		ESILCommand pokeCommand = (stack, env) ->
		{
			ignoreAccesses = true;
			ValueSet destinationAddress = stack.pop().execute(stack, env)
			                                   .getValue();
			ignoreAccesses = false;
			ValueSet value = stack.pop().execute(stack, env).getValue();
			return null;
		};
		commands.put(ESILKeyword.POKE, pokeCommand);
		commands.put(ESILKeyword.POKE_AST, pokeCommand);
		commands.put(ESILKeyword.POKE1, pokeCommand);
		commands.put(ESILKeyword.POKE2, pokeCommand);
		commands.put(ESILKeyword.POKE4, pokeCommand);
		commands.put(ESILKeyword.POKE8, pokeCommand);
		ESILCommand peekCommand = (stack, env) ->
		{
			ignoreAccesses = true;
			ValueSet address = stack.pop().execute(stack, env).getValue();
			ignoreAccesses = false;
			return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
		};
		commands.put(ESILKeyword.PEEK, peekCommand);
		commands.put(ESILKeyword.PEEK_AST, peekCommand);
		commands.put(ESILKeyword.PEEK1, peekCommand);
		commands.put(ESILKeyword.PEEK2, peekCommand);
		commands.put(ESILKeyword.PEEK4, peekCommand);
		commands.put(ESILKeyword.PEEK8, peekCommand);
		commands.put(ESILKeyword.ADD_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.ADD),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.SUB_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.SUB),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.MUL_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.MUL),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.DIV_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.DIV),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.MOD_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.MOD),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.SHIFT_LEFT_ASSIGN,
				new CompoundAssignCommand(
						commands.get(ESILKeyword.SHIFT_LEFT),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.SHIFT_RIGHT_ASSIGN,
				new CompoundAssignCommand(
						commands.get(ESILKeyword.SHIFT_RIGHT),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.AND_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.AND),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.OR_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.OR),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.XOR_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.XOR),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.INC_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.INC),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.DEC_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.DEC),
						commands.get(ESILKeyword.ASSIGNMENT)));
		commands.put(ESILKeyword.NEG_ASSIGN,
				new CompoundAssignCommand(commands.get(ESILKeyword.NEG),
						commands.get(ESILKeyword.ASSIGNMENT)));
		alocs = new HashMap<>();
	}

	public void analyse(BasicBlock block) {
		ignoreAccesses = false;
		alocs.clear();
		AbstractEnvironment env = loadMachineState(block);
		analyse(block, env);
	}

	private AbstractEnvironment loadMachineState(final BasicBlock block) {
		AbstractEnvironment env = new AbstractEnvironment();
		for (Edge edge : block.getEdges(Direction.OUT, "VALUE")) {
			try {
				String serializedValueSet = edge.getProperty("value");
				ByteArrayInputStream bi = new ByteArrayInputStream(
						Base64.getDecoder()
						      .decode(serializedValueSet.getBytes()));
				ObjectInputStream si = new ObjectInputStream(bi);
				ValueSet value = (ValueSet) si.readObject();
				Aloc aloc = (Aloc) edge.getVertex(Direction.IN);
				if (aloc.isRegister()) {
					env.setRegister(aloc.getName(), value);
				}
				alocs.put(aloc.getName(), aloc);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return env;
	}

	private void analyse(BasicBlock block, AbstractEnvironment env) {
		ESILTransformer transformer = new ESILTransformer(commands);
		transformer.observer = new DataObjectAccessObserver();
		for (Instruction instruction : block.orderedInstructions()) {
			this.instruction = instruction;
			String esilCode = instruction.getEsilCode();
			env = transformer.transform(esilCode, env);
		}
	}

	private class DataObjectAccessObserver<T>
			implements DataObjectObserver<T> {

		@Override
		public void updateRead(DataObject<T> dataObject) {
			if (null == instruction || ignoreAccesses) {
				return;
			}
			Aloc aloc = instructionToAloc(instruction,
					dataObject.getIdentifier().toString());
			if (aloc == null) {
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
			Aloc aloc = instructionToAloc(instruction,
					dataObject.getIdentifier().toString());
			if (aloc == null) {
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

	private static Aloc instructionToAloc(
			Instruction instruction, String alocName) {
		GremlinPipeline<Instruction, Aloc> pipe = new GremlinPipeline();
		pipe.start(instruction)
		    .in("IS_BB_OF")
		    .in("IS_FUNC_OF")
		    .out("ALOC_USE_EDGE")
		    .filter(v -> v.getProperty("name").equals(alocName));
		return pipe.hasNext() ? pipe.next() : null;
	}

}
