package bjoern.plugins.alocs;

import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.r2interface.Radare;
import bjoern.r2interface.architectures.Architecture;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.GraphHelper;

import java.io.IOException;
import java.util.*;

public class FunctionAlocCreator
{

	private static final String BELONGS_TO_EDGE = "BELONGS_TO";

	private Map<String, Vertex> registerToVertex = new HashMap<String,
			Vertex>();
	private Radare radare;
	private OrientGraphNoTx graph;
	private Function function;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph) throws
			IOException
	{
		this.radare = radare;
		this.graph = graph;
	}

	public void createAlocsForFunction(Function function) throws IOException
	{
		this.function = function;
		createAlocsForAllInstructions();
	}

	private void createAlocsForAllInstructions() throws IOException
	{
		List<Instruction> instructions = Traversals.functionToInstructions(
				function);
		for (Instruction instr : instructions)
		{
			createAlocsForInstruction(instr);
		}
	}

	private void createAlocsForInstruction(Instruction instr) throws
			IOException
	{
		createAlocsForRegisters(instr);
	}

	private void createAlocsForRegisters(Instruction instr) throws IOException
	{
		Long address = instr.getAddress();
		Set<String> registerNames = new HashSet<>();
		registerNames.addAll(radare.getRegistersRead(address));
		registerNames.addAll(radare.getRegistersWritten(address));
		for (String registerName : registerNames)
		{
			Vertex register;
			if (isFlag(registerName))
			{
				register = createFlagAloc(registerName);
			} else
			{
				register = createRegisterAloc(registerName);
				Vertex family = getRegisterFamilyNode(registerName);
				register.addEdge(BELONGS_TO_EDGE, family);
			}
			function.addEdge(Traversals.ALOC_USE_EDGE, register);
		}
	}

	private Vertex getRegisterFamilyNode(String registerName)
	{
		String registerFamily = radare.getRegisterFamily(registerName);
		if (!registerToVertex.containsKey(registerFamily))
		{
			registerToVertex
					.put(registerFamily, GraphHelper.addVertex(graph, 0));
		}
		return registerToVertex.get(registerFamily);
	}

	private Vertex createRegisterAloc(String alocName)
	{
		return createAloc(alocName, AlocTypes.REGISTER);
	}

	private Vertex createFlagAloc(String alocName)
	{
		return createAloc(alocName, AlocTypes.FLAG);
	}

	private Vertex createAloc(String alocName, String subType)
	{
		return GraphHelper.addVertex(graph, 0,
				BjoernNodeProperties.TYPE, BjoernNodeTypes.ALOC,
				BjoernNodeProperties.SUBTYPE, subType,
				BjoernNodeProperties.NAME, alocName);
	}

	private boolean isFlag(String registerName) throws IOException
	{
		Architecture architecture = radare.getArchitecture();
		return registerName.startsWith("$") || architecture
				.isFlag(registerName);
	}

}
