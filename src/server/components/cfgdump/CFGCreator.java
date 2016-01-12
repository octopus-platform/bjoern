package server.components.cfgdump;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class CFGCreator
{

	protected OrientGraphNoTx g;

	public CFGCreator(OrientGraphNoTx graph)
	{
		this.g = graph;
	}

	public Graph createCFG(Vertex functionNode)
	{
		CFGGraphWrapper sg = new CFGGraphWrapper(new TinkerGraph());
		sg.addVertex(functionNode);
		Iterable<Vertex> basicBlocks = functionNode.getVertices(Direction.OUT,
				"IS_FUNC_OF");
		// Connect basic blocks with instructions
		for (Vertex bb : basicBlocks)
		{
			sg.addVertex(bb);
			for (Edge edge : bb.getEdges(Direction.OUT, "IS_BB_OF"))
			{
				Vertex instr = edge.getVertex(Direction.IN);
				sg.addVertex(instr);
				sg.addEdge(edge);
			}
		}
		// Connect basic blocks with each other
		for (Vertex bb : basicBlocks)
		{
			for (Edge edge : bb.getEdges(Direction.OUT, "CFLOW_ALWAYS",
					"CFLOW_TRUE", "CFLOW_FALSE"))
			{
				Vertex head = edge.getVertex(Direction.IN);
				if (sg.contains(head))
				{
					sg.addEdge(edge);
				}
			}
			// Add edge from function node to first basic block
			if (bb.getProperty("addr").equals(functionNode.getProperty("addr")))
			{
				Graph graph = sg.getGraph();
				Vertex v = graph.getVertex(functionNode.getId());
				Vertex w = graph.getVertex(bb.getId());
				graph.addEdge("", v, w, "START");
			}

		}
		return sg.getGraph();
	}
}
