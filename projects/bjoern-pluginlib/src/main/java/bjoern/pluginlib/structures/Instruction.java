package bjoern.pluginlib.structures;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.Traversals;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import octopus.lib.structures.OctopusNode;

public class Instruction extends OctopusNode implements Comparable<Instruction>
{

	public Instruction(Vertex vertex)
	{
		super(vertex, NodeTypes.INSTRUCTION);
	}

	public long getAddress()
	{
		return Long.parseLong(getProperty(BjoernNodeProperties.ADDR).toString(), 16);
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

	public String getCode()
	{
		return getProperty("repr");
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
		final int maxLoops = 10000;
		return new GremlinPipeline<>(this.getBaseVertex()).as("start")
				.out(Traversals.INSTR_CFLOW_EDGE, Traversals.INSTR_CFLOW_TRANSITIVE_EDGE).dedup().loop("start",
						arg -> arg.getLoops() < maxLoops
								&& !arg.getObject().getProperty(BjoernNodeProperties.REPR).equals("ret")
								&& !arg.getObject().getProperty(BjoernNodeProperties.REPR).equals("jmp section..plt"),
						arg -> arg.getObject().getProperty(BjoernNodeProperties.REPR).equals("ret")
								|| arg.getObject().getProperty(BjoernNodeProperties.REPR).equals("jmp section..plt"))
				.transform(Instruction::new);
	}
}
