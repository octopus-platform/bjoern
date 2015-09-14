package unresolvedEdgeStore;

public class UnresolvedEdge
{
	private UnresolvedNode sourceNode;
	private UnresolvedNode destNode;

	public UnresolvedEdge(UnresolvedNode src, UnresolvedNode dst)
	{
		setSourceNode(src);
		setDestNode(dst);
	}

	public UnresolvedNode getSourceNode()
	{
		return sourceNode;
	}

	public void setSourceNode(UnresolvedNode sourceNode)
	{
		this.sourceNode = sourceNode;
	}

	public UnresolvedNode getDestNode()
	{
		return destNode;
	}

	public void setDestNode(UnresolvedNode destNode)
	{
		this.destNode = destNode;
	}

}
