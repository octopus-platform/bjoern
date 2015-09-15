package structures;

import java.util.LinkedList;
import java.util.List;

import nodeStore.Node;
import nodeStore.NodeTypes;
import structures.edges.DirectedEdge;

public class Function extends Node
{

	FunctionContent content = new FunctionContent();
	List<DirectedEdge> unresolvedEdges = new LinkedList<DirectedEdge>();

	private String name = "";

	public Function()
	{
		setType(NodeTypes.FUNCTION);
		setIsPermanent(true);
	}

	public FunctionContent getContent()
	{
		return content;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setContent(FunctionContent content)
	{
		this.content = content;
	}

	public void addUnresolvedEdge(DirectedEdge edge)
	{
		unresolvedEdges.add(edge);
	}

	public List<DirectedEdge> getUnresolvedEdges()
	{
		return unresolvedEdges;
	}

	public void deleteEdges()
	{
		unresolvedEdges = null;
	}

	public void deleteContent()
	{
		content = null;
	}

}
