package structures;

import java.util.Collection;
import java.util.List;

import nodeStore.Node;
import nodeStore.NodeTypes;

public class Function extends Node
{

	FunctionContent content = new FunctionContent();

	private String name = "";

	public Function()
	{
		setType(NodeTypes.FUNCTION);
	}

	public Collection<BasicBlock> getBasicBlocks()
	{
		return content.getBasicBlocks();
	}

	public List<CFGEdge> getEdges()
	{
		return content.getEdges();
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		BasicBlock block = content.getBasicBlockAtAddress(addr);

		// TODO: throw exception instead
		if (block != null)
		{
			System.err.println(
					"Warning: CFG contains multiple basic blocks with the same address");
			return;
		}

		content.addBasicBlock(addr, node);
	}

	public void addEdge(BasicBlock from, BasicBlock to, String type)
	{
		CFGEdge newEdge = new CFGEdge();
		newEdge.setFrom(from);
		newEdge.setTo(to);
		newEdge.setType(type);
		content.addEdge(newEdge);
	}

	public void addUnresolvedEdge(Long from, Long to)
	{
		content.addUnresolvedEdge(from, to);
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
