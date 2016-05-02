package bjoern.plugins.alocs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.GraphOperations;
import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.plugintypes.RadareProjectPlugin;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Node;
import bjoern.structures.BjoernNodeProperties;

public class AlocPlugin extends RadareProjectPlugin {

	OrientGraphNoTx graph;

	@Override
	public void execute() throws Exception
	{
		graph = orientConnector.getNoTxGraphInstance();
		Iterable<Vertex> allFunctions = LookupOperations.getAllFunctions(graph);

		createAlocsForFunctions(allFunctions);

		graph.shutdown();
	}

	private void createAlocsForFunctions(Iterable<Vertex> functions) throws IOException
	{
		for(Vertex func : functions)
		{
			createAlocsForFunction(func);
		}

	}

	private void createAlocsForFunction(Vertex vertex) throws IOException
	{
		createRegisterAlocs(vertex);

		BasicBlock entryBlock = Traversals.functionToEntryBlock(vertex);
		if(entryBlock == null){
			System.err.println("Warning: function without entry block");
			return;
		}

	}

	private void createRegisterAlocs(Vertex vertex) throws IOException
	{
		createNodesForRegistersUsedByFunction(vertex);
		createReadAndWriteEdgesFromFunctions(vertex);
	}

	private void createReadAndWriteEdgesFromFunctions(Vertex vertex)
	{
		// Determine all registers read by the function
		// Determine all registers written by the function
	}

	private void createNodesForRegistersUsedByFunction(Vertex functionVertex) throws IOException
	{
		String functionAddr = functionVertex.getProperty("addr");
		List<String> registers = radare.getRegistersUsedByFunc(functionAddr);
		for(String register : registers)
		{
			Vertex registerVertex = createRegisterNodeForFunctionAndRegister(functionAddr, register);
			linkFunctionAndRegister(functionVertex, registerVertex);
		}

	}

	private void linkFunctionAndRegister(Vertex functionVertex, Vertex registerVertex)
	{
		Node functionNode = new Node(functionVertex);
		Node registerNode = new Node(registerVertex);

		GraphOperations.addEdge(graph, functionNode, registerNode, GraphOperations.INSTR_CFLOW_EDGE);
	}

	private Vertex createRegisterNodeForFunctionAndRegister(String functionAddr, String register)
	{
		Map<String, String> properties = new HashMap<String,String>();

		properties.put(BjoernNodeProperties.ADDR, functionAddr);
		properties.put(BjoernNodeProperties.TYPE, NodeTypes.ALOC);
		properties.put(BjoernNodeProperties.NAME, register);

		return GraphOperations.addNode(graph, properties);
	}

}
