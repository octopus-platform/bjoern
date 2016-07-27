package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeTypes;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.Iterator;

public class BasicBlock extends BjoernNode
{

	private static final String[] CFLOW_EDGES = {EdgeTypes.CFLOW, EdgeTypes.CFLOW_TRUE, EdgeTypes.CFLOW_FALSE};

	public BasicBlock(Vertex vertex)
	{
		super(vertex, BjoernNodeTypes.BASIC_BLOCK);
	}

	public Instruction getEntry()
	{
		Iterator<Instruction> instructions = orderedInstructions().iterator();
		if (instructions.hasNext())
		{
			return instructions.next();
		}
		return null;
	}

	public Instruction getExit()
	{
		Iterator<Instruction> instructions = orderedInstructions().iterator();
		Instruction last = null;
		if (instructions.hasNext())
		{
			last = instructions.next();
		}
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
