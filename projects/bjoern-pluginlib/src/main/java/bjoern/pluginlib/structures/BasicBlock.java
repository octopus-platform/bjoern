package bjoern.pluginlib.structures;

import bjoern.nodeStore.NodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import octopus.lib.structures.OctopusNode;

import java.util.Iterator;

public class BasicBlock extends OctopusNode
{

	private static final String[] CFLOW_EDGES = {EdgeTypes.CFLOW, EdgeTypes.CFLOW_TRUE, EdgeTypes.CFLOW_FALSE};

	public BasicBlock(Vertex vertex)
	{
		super(vertex, NodeTypes.BASIC_BLOCK);
	}

	public Instruction getEntry()
	{
		Iterator<Instruction> instructions = orderedInstructions().iterator();
		return instructions.next();
	}

	public Instruction getExit()
	{
		Iterator<Instruction> instructions = orderedInstructions().iterator();
		Instruction last = instructions.next();
		while (instructions.hasNext())
		{
			last = instructions.next();
		}
		return last;
	}

	public GremlinPipeline<?, Instruction> instructions()
	{
		return new GremlinPipeline<>(this.getBaseVertex()).out(EdgeTypes.IS_BB_OF).transform(Instruction::new);
	}

	public GremlinPipeline<?, Instruction> orderedInstructions()
	{
		return instructions().order(pair -> pair.getA().compareTo(pair.getB()));
	}

	public GremlinPipeline<?, BasicBlock> cflow()
	{
		return new GremlinPipeline<>(this.getBaseVertex()).out(CFLOW_EDGES).transform(BasicBlock::new);
	}
}
