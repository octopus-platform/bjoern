package bjoern.pluginlib;

import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Traversals
{

	public final static String INSTR_CFLOW_EDGE = "NEXT_INSTR";
	public final static String ALOC_USE_EDGE = "ALOC_USE_EDGE";
	public static final String INSTR_CFLOW_TRANSITIVE_EDGE = "NEXT_INSTR_TRANSITIVE";

	public static BasicBlock functionToEntryBlock(Vertex func)
	{
		GremlinPipeline<Vertex, Vertex> pipe = createNewGremlinPipe();

		pipe.start(func).in(EdgeTypes.INTERPRETATION).out(EdgeTypes.INTERPRETATION).filter(
				v -> v.getProperty(BjoernNodeProperties.TYPE).equals
						(BjoernNodeTypes.BASIC_BLOCK)
		);

		Vertex vertex = getFirstVertexFromPipeOrRaise(pipe);
		return new BasicBlock(vertex);
	}

	public static List<Instruction> functionToInstructions(Vertex func)
	{
		GremlinPipeline<Vertex, Vertex> pipe = createNewGremlinPipe();

		pipe.start(func).out(EdgeTypes.IS_FUNCTION_OF).out(EdgeTypes.IS_BB_OF);

		List<Vertex> list = pipe.toList();

		return list.stream().map(Instruction::new).collect(Collectors.toList());
	}


	private static GremlinPipeline<Vertex, Vertex> createNewGremlinPipe()
	{
		return new GremlinPipeline<>();
	}

	private static Vertex getFirstVertexFromPipeOrRaise(GremlinPipeline<Vertex, Vertex> pipe)
	{
		if (!pipe.hasNext())
			throw new RuntimeException("Empty pipeline");

		Vertex vertex = pipe.next();

		if (vertex == null)
			throw new RuntimeException("Empty pipeline");

		return vertex;
	}

	public static Instruction functionToEntryInstruction(Function function)
	{
		GremlinPipeline<Vertex, Vertex> pipeline = createNewGremlinPipe();

		Vertex vertex;
		pipeline.start(function).in(EdgeTypes.INTERPRETATION).out(EdgeTypes.INTERPRETATION)
				.filter(v -> v.getProperty(BjoernNodeProperties.TYPE).equals(BjoernNodeTypes.INSTRUCTION));

		if (pipeline.hasNext())
		{
			return new Instruction(pipeline.next());
		} else
		{
			return null;
		}
	}

	public static List<Instruction> instructionToSuccessors(Instruction instruction)
	{
		return StreamSupport.stream(instruction
				.getVertices(Direction.OUT, INSTR_CFLOW_EDGE)
				.spliterator(), false)
				.map(Instruction::new).collect(Collectors.toList());
	}

	public static List<Aloc> functionToAlocs(Function function)
	{
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
		pipe.start(function).as("loop")
				.out(EdgeTypes.IS_FUNCTION_OF, EdgeTypes.IS_BB_OF, EdgeTypes.READ, EdgeTypes.WRITE)
				.loop("loop", v -> true,
						v -> v.getObject().getProperty(BjoernNodeProperties.TYPE).toString().equals(
								BjoernNodeTypes.ALOC)).dedup();
		return pipe.toList().stream().map(Aloc::new).collect(Collectors.toList());
	}

}
