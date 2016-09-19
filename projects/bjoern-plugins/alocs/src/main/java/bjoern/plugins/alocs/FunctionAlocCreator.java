package bjoern.plugins.alocs;

import bjoern.structures.BjoernNodeTypes;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.radare.emulation.esil.memaccess.ESILStackAccessEvaluator;
import bjoern.pluginlib.radare.emulation.esil.memaccess.MemoryAccess;
import bjoern.pluginlib.structures.BjoernNode;
import bjoern.pluginlib.structures.Instruction;
import bjoern.r2interface.Radare;
import bjoern.r2interface.architectures.Architecture;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.GraphOperations;
import octopus.lib.structures.OctopusNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionAlocCreator
{

	private Map<String, Vertex> registerToVertex = new HashMap<String, Vertex>();
	private Radare radare;
	private OrientGraphNoTx graph;
	private Vertex functionVertex;
	private ESILStackAccessEvaluator memAccessEvaluator;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph) throws IOException
	{
		this.radare = radare;
		this.graph = graph;
		this.memAccessEvaluator = new ESILStackAccessEvaluator(radare);
	}

	public void createAlocsForFunction(Vertex function) throws IOException
	{
		functionVertex = function;
		memAccessEvaluator.initializeForFunction(function);
		createAlocsForAllInstructions();
	}

	private void createAlocsForAllInstructions() throws IOException
	{
		List<Instruction> instructions = Traversals.functionToInstructions(functionVertex);
		for (Instruction instr : instructions)
		{
			createAlocsForInstruction(instr);
		}
	}

	private void createAlocsForInstruction(Instruction instr) throws IOException
	{
		long address = instr.getAddress();
		createAlocsForRegisters(instr, address);
		createAlocsForMemoryAccesses(instr, address);
	}

	private void createAlocsForRegisters(Instruction instr, long address) throws IOException
	{
		List<String> registersRead = radare.getRegistersRead(Long.toUnsignedString(address));
		createAlocsForRegisterList(instr, registersRead, EdgeTypes.READ);
		List<String> registersWritten = radare.getRegistersWritten(Long.toUnsignedString(address));
		createAlocsForRegisterList(instr, registersWritten, EdgeTypes.WRITE);
	}

	private void createAlocsForMemoryAccesses(Instruction instr, long address) throws IOException
	{

		List<MemoryAccess> access = memAccessEvaluator.extractMemoryAccesses(instr);
		for (MemoryAccess m : access)
		{
			createAloc(m.getEsilExpression(), AlocTypes.LOCAL);
			m.debugOut();
		}
	}

	private void createAlocsForRegisterList(Instruction instr, List<String> registersRead, String edgeType) throws
			IOException
	{
		for (String registerStr : registersRead)
		{

			Vertex registerVertex = registerToVertex.get(registerStr);
			if (registerVertex == null)
			{

				registerVertex = createAloc(registerStr, subTypeFromAlocName(registerStr));
			}
		}
	}

	private Vertex createAloc(String alocName, String subType) throws IOException
	{
		String functionAddr = functionVertex.getProperty("addr");

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(BjoernNodeProperties.ADDR, functionAddr);
		properties.put(BjoernNodeProperties.TYPE, BjoernNodeTypes.ALOC);
		properties.put(BjoernNodeProperties.SUBTYPE, subType);
		properties.put(BjoernNodeProperties.NAME, alocName);

		Vertex alocVertex = GraphOperations.addNode(graph, properties);
		registerToVertex.put(alocName, alocVertex);
		linkFunctionAndAloc(alocVertex);
		return alocVertex;
	}

	/**
	 * Determines the subtype by register name, e.g., register, flag, local, ...
	 *
	 * @throws IOException
	 */

	private String subTypeFromAlocName(String alocName) throws IOException
	{
		Architecture architecture = radare.getArchitecture();

		if (alocName.startsWith("$") || architecture.isFlag(alocName))
			return AlocTypes.FLAG;


		return AlocTypes.REGISTER;
	}

	private void linkFunctionAndAloc(Vertex alocVertex)
	{
		OctopusNode functionNode = new BjoernNode(functionVertex);
		OctopusNode alocNode = new BjoernNode(alocVertex);

		GraphOperations.addEdge(graph, functionNode, alocNode, Traversals.ALOC_USE_EDGE);
	}

}
