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
	Vertex functionVertex;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph)
	{
		this.radare = radare;
		this.graph = graph;
	}

	public void createAlocsForFunction(Vertex function) throws IOException
	{
		functionVertex = function;

		createRegisterAlocs();

		try{
			BasicBlock entryBlock = Traversals.functionToEntryBlock(function);
		} catch(RuntimeException ex) {
			System.err.println("Warning: function without entry block");
			return;
		}
	}

	private void createRegisterAlocs() throws IOException
	{
		List<Instruction> instructions = Traversals.functionToInstructions(functionVertex);
		for(Instruction instr : instructions){
			createEdgesFromInstructionToAlocs(instr);
		}
	}

	private void createEdgesFromInstructionToAlocs(Instruction instr) throws IOException
	{
		long address = instr.getAddress();
		List<String> registersRead = radare.getRegistersRead(Long.toUnsignedString(address));
		List<String> registersWritten = radare.getRegistersWritten(Long.toUnsignedString(address));

		createAlocsForRegisters(instr, registersRead, EdgeTypes.READ);
		createAlocsForRegisters(instr, registersWritten, EdgeTypes.WRITE);
	}

	private void createAlocsForRegisters(Instruction instr, List<String> registersRead, String edgeType) {
		for(String registerStr : registersRead){

			Vertex registerVertex = registerToVertex.get(registerStr);
			if(registerVertex == null){
				registerVertex = createAloc(registerStr);
			}
			GraphOperations.addEdge(graph, instr, new Node(registerVertex), edgeType);
		}
	}

	private Vertex createAloc(String alocName)
	{
		String functionAddr = functionVertex.getProperty("addr");

		Map<String, String> properties = new HashMap<String,String>();
		properties.put(BjoernNodeProperties.ADDR, functionAddr);
		properties.put(BjoernNodeProperties.TYPE, NodeTypes.ALOC);
		properties.put(BjoernNodeProperties.NAME, alocName);

		Vertex alocVertex = GraphOperations.addNode(graph, properties);
		registerToVertex.put(alocName, alocVertex);
		linkFunctionAndRegister(alocVertex);
		return alocVertex;
	}

	private void linkFunctionAndRegister(Vertex alocVertex)
	{
		Node functionNode = new Node(functionVertex);
		Node alocNode = new Node(alocVertex);

		GraphOperations.addEdge(graph, functionNode, alocNode, GraphOperations.ALOC_USE_EDGE);
	}

}
