package exporters.structures.interpretations;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;
import exporters.radare.inputModule.RadareDisassemblyParser;
import exporters.radare.inputModule.exceptions.InvalidDisassembly;
import exporters.structures.annotations.VariableOrArgument;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.edges.EdgeTypes;

public class FunctionContent
{
	private final long functionAddr;
	HashMap<Long, BasicBlock> basicBlocks = new HashMap<Long, BasicBlock>();
	List<DirectedEdge> edges = new LinkedList<DirectedEdge>();
	List<DirectedEdge> keyedEdges = new LinkedList<DirectedEdge>();
	Disassembly disassembly = new Disassembly(0);

	public FunctionContent(long functionAddr)
	{
		this.functionAddr = functionAddr;
	}

	public Collection<BasicBlock> getBasicBlocks()
	{
		return basicBlocks.values();
	}

	public List<DirectedEdge> getEdges()
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

	public void addCFGEdge(DirectedEdge newEdge)
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

		keyedEdges.add(edge);
	}

	public void registerBasicBlock(long addr, BasicBlock node)
	{
		BasicBlock block = getBasicBlockAtAddress(addr);

		if (block != null)
			throw new RuntimeException("Duplicate Node");

		addBasicBlock(addr, node);
	}

	public void addEdge(BasicBlock from, BasicBlock to, String type)
	{
		DirectedEdge newEdge = new DirectedEdge();

		newEdge.setSourceNode(from);
		newEdge.setDestNode(to);
		addCFGEdge(newEdge);
	}

	public List<DirectedEdge> getUnresolvedEdges()
	{
		return keyedEdges;
	}

	public void consumeDisassembly(String disassemblyStr)
	{
		RadareDisassemblyParser parser = new RadareDisassemblyParser();
		try
		{
			disassembly = parser.parse(disassemblyStr, functionAddr);
		}
		catch (InvalidDisassembly e)
		{
			// TODO: might want to log this error.
		}
	}

}
