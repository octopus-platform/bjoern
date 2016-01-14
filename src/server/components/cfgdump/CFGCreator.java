package server.components.cfgdump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class CFGCreator
{

	private static final Logger logger = LoggerFactory
			.getLogger(CFGCreator.class);

	protected OrientGraphNoTx g;

	public CFGCreator(OrientGraphNoTx graph)
	{
		this.g = graph;
	}

	public Graph createCFG(Vertex func)
	{
		CFGGraphWrapper cfg = new CFGGraphWrapper(new TinkerGraph());
		cfg.addVertex(func);

		// Create hierarchy
		for (Edge isFuncOfEdge : func.getEdges(Direction.OUT, "IS_FUNC_OF"))
		{
			Vertex bb = isFuncOfEdge.getVertex(Direction.IN);
			cfg.addVertex(bb);
			cfg.addEdge(isFuncOfEdge);
			for (Edge isBBOfEdge : bb.getEdges(Direction.OUT, "IS_BB_OF"))
			{
				Vertex instr = isBBOfEdge.getVertex(Direction.IN);
				cfg.addVertex(instr);
				cfg.addEdge(isBBOfEdge);
			}
		}

		// Add control flow edges
		for (Vertex bb : func.getVertices(Direction.OUT, "IS_FUNC_OF"))
		{
			for (Edge cflow : bb.getEdges(Direction.OUT, "CFLOW_ALWAYS",
					"CFLOW_TRUE", "CFLOW_FALSE"))
			{
				cfg.addEdge(cflow);
			}
		}

		// Add address node
		for (Vertex addr : func.getVertices(Direction.IN, "INTERPRETABLE_AS"))
		{
			cfg.addVertex(addr);
			for (Edge interpretableAsEdge : addr.getEdges(Direction.OUT,
					"INTERPRETABLE_AS"))
			{
				cfg.addEdge(interpretableAsEdge);
			}
		}

		return cfg.getGraph();
	}
}
