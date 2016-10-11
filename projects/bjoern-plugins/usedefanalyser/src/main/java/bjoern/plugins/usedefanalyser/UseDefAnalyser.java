package bjoern.plugins.usedefanalyser;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.data.DataObject;
import bjoern.plugins.vsa.data.DataObjectObserver;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.memrgn.LocalRegion;
import bjoern.plugins.vsa.domain.memrgn.MemoryRegion;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.*;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.FlagContainer;
import bjoern.plugins.vsa.transformer.esil.stack.RegisterContainer;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Deque;
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
		commands.put(ESILKeyword.ASSIGNMENT,
				new AssignmentCommand());
		ESILCommand relationalCommand = new RelationalCommand();
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
		ESILCommand pokeCommand = new UseDefAnalyser.PokeCommand();
		commands.put(ESILKeyword.POKE, pokeCommand);
		commands.put(ESILKeyword.POKE_AST, pokeCommand);
		commands.put(ESILKeyword.POKE1, pokeCommand);
		commands.put(ESILKeyword.POKE2, pokeCommand);
		commands.put(ESILKeyword.POKE4, pokeCommand);
		commands.put(ESILKeyword.POKE8, pokeCommand);
		ESILCommand peekCommand = new UseDefAnalyser.PeekCommand();
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
		ESILTransformer transformer = new ESILTransformer(commands,
				UseDefAnalyser.PopCommand::new);
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
		    .has("name", alocName);
		return pipe.hasNext() ? pipe.next() : null;
	}

	private static void createEdgeIfNotExist(
			Vertex source, Vertex destination, String label) {
		for (Edge edge : source.getEdges(Direction.OUT, label)) {
			if (edge.getVertex(Direction.IN).equals(destination)) {
				// edge exists -> skip
				return;
			}
		}
		// add read edge from instruction to aloc
		source.addEdge(label, destination);
	}


	private class AssignmentCommand implements ESILCommand {

		@Override
		public ESILStackItem execute(
				final Deque<ESILCommand> stack,
				final AbstractEnvironment env) {
			Aloc aloc;

			ignoreAccesses = true;
			ESILStackItem item = stack.pop().execute(stack, env);
			ignoreAccesses = false;
			if (item instanceof RegisterContainer) {
				RegisterContainer registerContainer = (RegisterContainer) item;
				DataObject<ValueSet> register = registerContainer.getRegister();
				env.setRegister(register.getIdentifier(),
						stack.pop().execute(stack, env).getValue());
				aloc = instructionToAloc(instruction,
						register.getIdentifier().toString());
			} else if (item instanceof FlagContainer) {
				FlagContainer flagContainer = (FlagContainer) item;
				DataObject<Bool3> flag = flagContainer.getFlag();
				ValueSet valueSet = stack.pop()
				                         .execute(stack, env)
				                         .getValue();
				if (valueSet.isGlobal()) {
					StridedInterval stridedInterval = valueSet
							.getValueOfGlobalRegion();
					aloc = instructionToAloc(instruction,
							flag.getIdentifier().toString());
					if (stridedInterval.isZero()) {
						env.setFlag(flag.getIdentifier(), Bool3.FALSE);
					} else if (stridedInterval.isOne()) {
						env.setFlag(flag.getIdentifier(), Bool3.TRUE);
					} else {
						env.setFlag(flag.getIdentifier(), Bool3.MAYBE);
					}
				} else {
					throw new ESILTransformationException(
							"Error while executing assignment command: Cannot "
									+ "assign "
									+ valueSet + " to flag");
				}
			} else {
				throw new ESILTransformationException(
						"Error while executing assignment command");
			}
			if (aloc != null) {
				createEdgeIfNotExist(instruction, aloc, "WRITE");
			}
			return null;
		}
	}

	public class PokeCommand implements ESILCommand {

		@Override
		public ESILStackItem execute(
				Deque<ESILCommand> stack, AbstractEnvironment env) {
			ignoreAccesses = true;
			ValueSet value1 = stack.pop().execute(stack, env).getValue();
			ignoreAccesses = false;
			ValueSet value2 = stack.pop().execute(stack, env).getValue();
			if (env == null) {
				return null;
			}
			StridedInterval addresses = getValueOfLocalRegion(value1);
			ValueSet tmp = env.getRegister("rbp");
			if (tmp == null) {
				return null;
			}
			StridedInterval rbp = getValueOfLocalRegion(tmp);
			addresses = addresses.sub(rbp);
			if (addresses.isSingletonSet()) {
				for (long address : addresses.values()) {
					addWriteEdge(address);
					env.setLocalVariable(address, value2);
				}
			}
			// this command returns nothing/no item is pushed on the stack
			return null;
		}

		private void addWriteEdge(final long address) {
			GremlinPipeline<Instruction, Aloc> pipe = new GremlinPipeline();
			pipe.start(instruction)
			    .in("IS_BB_OF")
			    .in("IS_FUNC_OF")
			    .out("ALOC_USE_EDGE")
			    .has("offset", (int) address);
			if (pipe.hasNext()) {
				createEdgeIfNotExist(instruction, pipe.next(), "WRITE");
			}
		}

		private StridedInterval getValueOfLocalRegion(ValueSet valueSet) {
			if (valueSet.isTop()) {
				return StridedInterval.getTop(valueSet.getDataWidth());
			}
			for (MemoryRegion region : valueSet.getRegions()) {
				if (region instanceof LocalRegion) {
					return valueSet.getValueOfRegion(region);
				}
			}
			return StridedInterval.getBottom(valueSet.getDataWidth());
		}
	}

	public class PeekCommand implements ESILCommand {

		@Override
		public ESILStackItem execute(
				Deque<ESILCommand> stack, AbstractEnvironment env) {
			ignoreAccesses = true;
			ValueSet value = stack.pop().execute(stack, env).getValue();
			ignoreAccesses = false;
			StridedInterval bpOffset = getValueOfLocalRegion(value);
			ValueSet tmp = env.getRegister("rbp");
			if (tmp != null) {
				StridedInterval rbp = getValueOfLocalRegion(tmp);
				StridedInterval spOffset = bpOffset.sub(rbp);
				if (spOffset.isSingletonSet()) {
					for (long address : spOffset.values()) {
						ValueSet data = env.getLocalVariable(address);
						if (data != null) {
							createReadEdge(address);
							return new ValueSetContainer(data);
						}
					}
				}
			}
			return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
		}

		private void createReadEdge(final long address) {
			GremlinPipeline<Instruction, Aloc> pipe = new GremlinPipeline();
			pipe.start(instruction)
			    .in("IS_BB_OF")
			    .in("IS_FUNC_OF")
			    .out("ALOC_USE_EDGE")
			    .has("offset", (int) address);
			if (pipe.hasNext()) {
				createEdgeIfNotExist(instruction, pipe.next(), "READ");
			}
		}


		private StridedInterval getValueOfLocalRegion(ValueSet valueSet) {
			if (valueSet.isTop()) {
				return StridedInterval.getTop(valueSet.getDataWidth());
			}
			for (MemoryRegion region : valueSet.getRegions()) {
				if (region instanceof LocalRegion) {
					StridedInterval interval = valueSet.getValueOfRegion(
							region);
					return interval;
				}
			}
			return StridedInterval.getBottom(valueSet.getDataWidth());
		}
	}

	public class PopCommand extends
	                        bjoern.plugins.vsa.transformer.esil.commands.PopCommand {

		private DataObject<?> dataObject;

		public PopCommand(final ESILStackItem item) {
			super(item);
			if (item instanceof RegisterContainer) {
				dataObject = ((RegisterContainer) item).getRegister();
			} else if (item instanceof FlagContainer) {
				dataObject = ((FlagContainer) item).getFlag();
			}
		}

		@Override
		public ESILStackItem execute(
				final Deque<ESILCommand> stack,
				final AbstractEnvironment env) {
			createReadEdge();
			return super.execute(stack, env);
		}

		private void createReadEdge() {
			if (null == instruction || null == dataObject || ignoreAccesses) {
				return;
			}
			Aloc aloc = instructionToAloc(instruction,
					dataObject.getIdentifier().toString());
			if (aloc == null) {
				return;
			}
			createEdgeIfNotExist(instruction, aloc, "READ");
		}
	}
}
