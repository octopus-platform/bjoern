package bjoern.plugins.alocs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import bjoern.nodeStore.NodeTypes;
import bjoern.pluginlib.GraphOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.BasicBlock;
import bjoern.pluginlib.structures.Instruction;
import bjoern.pluginlib.structures.Node;
import bjoern.r2interface.Radare;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;

public class FunctionAlocCreator {

	Map<String,Vertex> registerToVertex = new HashMap<String,Vertex>();
	private Radare radare;
	private OrientGraphNoTx graph;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph)
	{
		this.radare = radare;
		this.graph = graph;
	}

	public void createAlocsForFunction(Vertex function) throws IOException
	{
		createRegisterAlocs(function);

		try{
			BasicBlock entryBlock = Traversals.functionToEntryBlock(function);
		} catch(RuntimeException ex) {
			System.err.println("Warning: function without entry block");
			return;
		}
	}

	private void createRegisterAlocs(Vertex vertex) throws IOException
	{
		createNodesForAllRegistersUsedByFunction(vertex);
		createEdgesFromInstructionsToAlocs(vertex);
	}

	private void createNodesForAllRegistersUsedByFunction(Vertex functionVertex) throws IOException
	{
		System.out.println("====");
		String functionAddr = functionVertex.getProperty("addr");
		List<String> registers = radare.getRegistersUsedByFunc(functionAddr);
		for(String registerStr : registers)
		{
			System.out.println(registerStr);
			System.out.println(registerStr.length());
			Vertex registerVertex = createRegisterNodeForFunctionAndRegister(functionAddr, registerStr);
			registerToVertex.put(registerStr, registerVertex);
			linkFunctionAndRegister(functionVertex, registerVertex);
		}
	}

	private void linkFunctionAndRegister(Vertex functionVertex, Vertex registerVertex)
	{
		Node functionNode = new Node(functionVertex);
		Node registerNode = new Node(registerVertex);

		GraphOperations.addEdge(graph, functionNode, registerNode, GraphOperations.ALOC_USE_EDGE);
	}

	private Vertex createRegisterNodeForFunctionAndRegister(String functionAddr, String register)
	{
		Map<String, String> properties = new HashMap<String,String>();

		properties.put(BjoernNodeProperties.ADDR, functionAddr);
		properties.put(BjoernNodeProperties.TYPE, NodeTypes.ALOC);
		properties.put(BjoernNodeProperties.NAME, register);

		return GraphOperations.addNode(graph, properties);
	}

	private void createEdgesFromInstructionsToAlocs(Vertex vertex) throws IOException
	{
		List<Instruction> instructions = Traversals.functionToInstructions(vertex);
		for(Instruction instr : instructions){
			createEdgesFromInstructionToAlocs(instr);
		}
	}

	private void createEdgesFromInstructionToAlocs(Instruction instr) throws IOException
	{
		long address = instr.getAddress();
		List<String> registersRead = radare.getRegistersRead(Long.toUnsignedString(address));
		for(String registerStr : registersRead){

			Vertex registerVertex = registerToVertex.get(registerStr);
			if(registerVertex == null){
				throw new RuntimeException("No vertex for register: " + registerStr);
			}
			GraphOperations.addEdge(graph, instr, new Node(registerVertex), EdgeTypes.READ);
		}
	}



}
