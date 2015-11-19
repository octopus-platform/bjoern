package exporters.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;
import exporters.nodeStore.exceptions.DuplicateNode;
import exporters.radare.inputModule.RadareDisassemblyParser;
import exporters.radare.inputModule.exceptions.InvalidDisassembly;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.edges.EdgeTypes;
import exporters.structures.edges.ResolvedCFGEdge;

public class FunctionContent
{
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<ResolvedCFGEdge> edges = new LinkedList<ResolvedCFGEdge>();
	List<DirectedEdge> unresolvedEdges = new LinkedList<DirectedEdge>();
	Disassembly disassembly = new Disassembly();

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<ResolvedCFGEdge> getEdges()
	{
		return edges;
	}

	public BasicBlock getBasicBlockAtAddress(long addr)
	{
		return basicBlocks.get(addr);
	}

	public DisassemblyLine getDisassemblyLineForAddr(long addr)
	{
		return disassembly.getLineForAddr(addr);
	}

	public List<VariableOrArgument> getVariablesAndArguments()
	{
		return disassembly.getVariablesAndArguments();
	}

	public void addBasicBlock(long addr, BasicBlock node)
	{
		// TODO: It should be enough to pass node and
		// read its address from the respective field.
		basicBlocks.put(addr, node);
	}

	public void addCFGEdge(ResolvedCFGEdge newEdge)
	{
		edges.add(newEdge);
	}

	public void addUnresolvedEdge(Long from, Long to, String type)
	{
		Node src = new Node();
		src.setAddr(from);
		src.setType(NodeTypes.BASIC_BLOCK);

		Node dst = new Node();
		dst.setAddr(to);
		dst.setType(NodeTypes.BASIC_BLOCK);

		DirectedEdge edge = new DirectedEdge();
		edge.setSourceNode(src);
		edge.setDestNode(dst);

		if (type.equals("jump"))
			edge.setType(EdgeTypes.CFLOW_TRUE);
		else
			edge.setType(EdgeTypes.CFLOW_FALSE);

		unresolvedEdges.add(edge);
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		BasicBlock block = getBasicBlockAtAddress(addr);

		if (block != null)
			throw new DuplicateNode();

		addBasicBlock(addr, node);
	}

	public void addEdge(BasicBlock from, BasicBlock to, String type)
	{
		ResolvedCFGEdge newEdge = new ResolvedCFGEdge();
		newEdge.setFrom(from);
		newEdge.setTo(to);
		newEdge.setType(type);
		addCFGEdge(newEdge);
	}

	public List<DirectedEdge> getUnresolvedEdges()
	{
		return unresolvedEdges;
	}

	public void consumeDisassembly(String disassemblyStr)
	{
		RadareDisassemblyParser parser = new RadareDisassemblyParser();
		try
		{
			disassembly = parser.parse(disassemblyStr);
		} catch (InvalidDisassembly e)
		{
			// TODO: might want to log this error.
		}
	}

}
