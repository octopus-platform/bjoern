package exporters.structures.edges;

import exporters.structures.BasicBlock;

public class ResolvedCFGEdge extends DirectedEdge
{

	public BasicBlock getFrom()
	{
		return (BasicBlock) this.sourceNode;
	}

	public void setFrom(BasicBlock from)
	{
		this.sourceNode = from;
	}

	public BasicBlock getTo()
	{
		return (BasicBlock) destNode;
	}

	public void setTo(BasicBlock to)
	{
		this.destNode = to;
	}

}
