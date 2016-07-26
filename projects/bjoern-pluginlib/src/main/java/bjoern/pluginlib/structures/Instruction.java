package bjoern.pluginlib.structures;

import bjoern.pluginlib.Traversals;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class Instruction extends BjoernNode implements Comparable<Instruction>
{

	public Instruction(Vertex vertex)
	{
		super(vertex, BjoernNodeTypes.INSTRUCTION);
	}


	public String getEsilCode()
	{
		return getProperty(BjoernNodeProperties.ESIL);
	}

	@Override
	public int compareTo(Instruction instruction)
	{
		if (this.getAddress() < instruction.getAddress())
		{
			return -1;
		} else if (this.getAddress() > instruction.getAddress())
		{
			return 1;
		} else
		{
			return 0;
		}
	}

	public boolean isCall()
	{
		return call().hasNext();
	}

	public GremlinPipeline<?, Instruction> call()
	{
		return new GremlinPipeline<>(this.getBaseVertex()).out(EdgeTypes.CALL).transform(Instruction::new);
	}

	public GremlinPipeline<?, Instruction> exits()
	{
		final int MAX_LOOPS = 10000;
		final String[] EDGES = {Traversals.INSTR_CFLOW_EDGE, Traversals.INSTR_CFLOW_TRANSITIVE_EDGE};
		if (!this.getVertices(Direction.OUT, EDGES).iterator().hasNext())
		{
			return new GremlinPipeline<>(this);
		} else
		{
			return new GremlinPipeline<>(this.getBaseVertex()).as("start")
					.out(EDGES).dedup().loop("start",
							arg -> arg.getLoops() < MAX_LOOPS,
							arg -> arg.getLoops() < MAX_LOOPS
									&& !arg.getObject().getEdges(Direction.OUT, EDGES).iterator().hasNext())
					.dedup()
					.transform(Instruction::new);
		}
	}
}
