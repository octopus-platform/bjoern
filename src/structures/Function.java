package structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import nodeStore.Node;
import nodeStore.NodeTypes;

import org.apache.commons.lang3.tuple.Pair;

public class Function extends Node
{

	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<CFGEdge> edges = new LinkedList<CFGEdge>();
	List<Pair<Long, Long>> unresolvedEdges = new LinkedList<Pair<Long, Long>>();
	private String name = "";

	public Function()
	{
		setType(NodeTypes.FUNCTION);
	}

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<CFGEdge> getEdges()
	{
		return edges;
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		if (basicBlocks.get(addr) != null)
		{
			System.err
					.println("Warning: CFG contains multiple basic blocks with the same address");
			return;
		}

		basicBlocks.put(addr, node);
	}

	public void addEdge(BasicBlock from, BasicBlock to, String type)
	{
		CFGEdge newEdge = new CFGEdge();
		newEdge.setFrom(from);
		newEdge.setTo(to);
		newEdge.setType(type);
		edges.add(newEdge);
	}

	public void addUnresolvedEdge(Long from, Long to)
	{
		Pair<Long, Long> pair = Pair.of(from, to);
		unresolvedEdges.add(pair);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

}
