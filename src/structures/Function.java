package structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Function
{
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<CFGEdge> edges = new LinkedList<CFGEdge>();

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

}
