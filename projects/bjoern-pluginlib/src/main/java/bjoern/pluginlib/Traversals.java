package bjoern.pluginlib;

import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Traversals {

	public final static String INSTR_CFLOW_EDGE = "NEXT_INSTR";
	public final static String ALOC_USE_EDGE = "ALOC_USE_EDGE";
	public static final String INSTR_CFLOW_TRANSITIVE_EDGE = "NEXT_INSTR_TRANSITIVE";

	public static BasicBlock functionToEntryBlock(Function func) {
		GremlinPipeline<Function, BasicBlock> pipe = new GremlinPipeline<>();

		pipe.start(func)
		    .in(EdgeTypes.INTERPRETATION)
		    .out(EdgeTypes.INTERPRETATION)
		    .filter(
				    v -> v.getProperty(BjoernNodeProperties.TYPE).equals
						    (BjoernNodeTypes.BASIC_BLOCK)
		    );

		if (pipe.hasNext()) {
			return pipe.next();
		} else {
			return null;
		}
	}

	public static List<Instruction> functionToInstructions(Function func) {
		GremlinPipeline<Function, Instruction> pipe = new GremlinPipeline<>();

		pipe.start(func)
		    .out(EdgeTypes.IS_FUNCTION_OF)
		    .out(EdgeTypes.IS_BB_OF);

		return pipe.toList();
	}

	public static Instruction functionToEntryInstruction(Function function) {
		GremlinPipeline<Function, Instruction> pipeline = new GremlinPipeline<>();

		pipeline.start(function)
		        .in(EdgeTypes.INTERPRETATION)
		        .out(EdgeTypes.INTERPRETATION)
		        .filter(v -> v.getProperty(BjoernNodeProperties.TYPE)
		                      .equals(BjoernNodeTypes.INSTRUCTION));

		if (pipeline.hasNext()) {
			return pipeline.next();
		} else {
			return null;
		}
	}

	public static List<Instruction> instructionToSuccessors(
			Instruction instruction) {
		return StreamSupport.stream(instruction
				.getVertices(Direction.OUT, INSTR_CFLOW_EDGE)
				.spliterator(), false)
		                    .map(Instruction::new)
		                    .collect(Collectors.toList());
	}

	public static List<Aloc> functionToAlocs(Function function) {
		GremlinPipeline<Function, Aloc> pipe = new GremlinPipeline<>();
		pipe.start(function).out(ALOC_USE_EDGE).dedup().cast(Aloc.class);
		return pipe.toList();
	}

	public static List<BasicBlock> blockToSuccessors(final BasicBlock n) {
		return StreamSupport.stream(
				n.getVertices(Direction.OUT, "CFLOW_ALWAYS", "CFLOW_TRUE",
						"CFLOW_FALSE").spliterator(), false)
		                    .map(x -> (BasicBlock) x)
		                    .collect(Collectors.toList());
	}
}
