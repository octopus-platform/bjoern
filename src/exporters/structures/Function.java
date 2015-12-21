package exporters.structures;

import java.util.LinkedList;
import java.util.List;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;
import exporters.structures.edges.DirectedEdge;


public class Function extends Node
{

	FunctionContent content;
	List<DirectedEdge> keyedEdges = new LinkedList<DirectedEdge>();

	private String name = "";

	public Function(long addr)
	{
		content = new FunctionContent(addr);
		setType(NodeTypes.FUNCTION);
		setIsPermanent(true);
		setAddr(addr);
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

	public void addKeyedEdge(DirectedEdge edge)
	{
		keyedEdges.add(edge);
	}

	public List<DirectedEdge> getUnresolvedEdges()
	{
		return keyedEdges;
	}

	public void deleteEdges()
	{
		keyedEdges = null;
	}

	public void deleteContent()
	{
		content = null;
	}

}
