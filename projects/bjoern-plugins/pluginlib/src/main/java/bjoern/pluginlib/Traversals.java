package bjoern.pluginlib;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Traversals
{

	public static BasicBlock functionToEntryBlock(Vertex func)
	{
		GremlinPipeline<Vertex, Vertex> pipe = createNewGremlinPipe();

		pipe.start(func).in(EdgeTypes.INTERPRETATION).out(EdgeTypes.INTERPRETATION).filter(
				v -> v.getProperty(BjoernNodeProperties.TYPE).equals
						(NodeTypes.BASIC_BLOCK)
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
		pipeline.start(function.getNode()).in(EdgeTypes.INTERPRETATION).out(EdgeTypes.INTERPRETATION)
				.filter(v -> v.getProperty(BjoernNodeProperties.TYPE).equals(NodeTypes.INSTRUCTION));

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
		return StreamSupport.stream(instruction.getNode()
				.getVertices(Direction.OUT, GraphOperations.INSTR_CFLOW_EDGE)
				.spliterator(), false)
				.map(Instruction::new).collect(Collectors.toList());
	}

}
