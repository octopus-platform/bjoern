package structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import nodeStore.NodeTypes;

public class FunctionContent
{
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<CFGEdge> edges = new LinkedList<CFGEdge>();
	List<UnresolvedEdge> unresolvedEdges = new LinkedList<UnresolvedEdge>();

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<CFGEdge> getEdges()
	{
		return edges;
	}

	public BasicBlock getBasicBlockAtAddress(long addr)
	{
		return basicBlocks.get(addr);
	}

	public void addBasicBlock(long addr, BasicBlock node)
	{
		// TODO: It should be enough to pass node and
		// read its address from the respective field.
		basicBlocks.put(addr, node);
	}

	public void addCFGEdge(CFGEdge newEdge)
	{
		edges.add(newEdge);
	}

	public void addUnresolvedEdge(Long from, Long to)
	{
		Pair<Long, Long> pair = Pair.of(from, to);

		UnresolvedNode src = new UnresolvedNode(from, NodeTypes.BASIC_BLOCK);
		UnresolvedNode dst = new UnresolvedNode(to, NodeTypes.BASIC_BLOCK);
		UnresolvedEdge edge = new UnresolvedEdge(src, dst);
		unresolvedEdges.add(edge);
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		BasicBlock block = getBasicBlockAtAddress(addr);

		// TODO: throw exception instead
		if (block != null)
		{
			System.err.println(
					"Warning: CFG contains multiple basic blocks with the same address");
			return;
		}

		addBasicBlock(addr, node);
	}

	public void addEdge(BasicBlock from, BasicBlock to, String type)
	{
		CFGEdge newEdge = new CFGEdge();
		newEdge.setFrom(from);
		newEdge.setTo(to);
		newEdge.setType(type);
		addCFGEdge(newEdge);
	}

}
