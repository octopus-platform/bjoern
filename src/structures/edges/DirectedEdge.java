package structures.edges;

import nodeStore.Node;

public class DirectedEdge
{
	Node sourceNode;
	Node destNode;
	String type;

	public Node getSourceNode()
	{
		return sourceNode;
	}

	public void setSourceNode(Node sourceNode)
	{
		this.sourceNode = sourceNode;
	}

	public Node getDestNode()
	{
		return destNode;
	}

	public void setDestNode(Node destNode)
	{
		this.destNode = destNode;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
