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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FunctionAlocCreator
{
	private static final String BELONGS_TO_EDGE = "BELONGS_TO";
	private static final String FAMILY_TYPE = "RegisterFamily";

	private Map<String, Vertex> registerFamilyCache = new HashMap<>();
	private Radare radare;
	private OrientGraphNoTx graph;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph) throws
			IOException
	{
		this.radare = radare;
		this.graph = graph;
	}

	public void createAlocsForFunction(Function function) throws IOException
	{
		for (String registerName : getRegisterNames(function))
		{
			Vertex aloc = createAlocForRegister(registerName);
			function.addEdge(Traversals.ALOC_USE_EDGE, aloc);
		}
	}

	private Set<String> getRegisterNames(Function function) throws IOException
	{
		Set<String> registerNames = new HashSet<>();
		for (Instruction instruction : Traversals
				.functionToInstructions(function))
		{
			registerNames.addAll(getRegisterNames(instruction));

		}
		return registerNames;
	}

	private Set<String> getRegisterNames(Instruction instruction) throws
			IOException
	{
		Long address = instruction.getAddress();
		Set<String> registerNames = new HashSet<>();
		registerNames.addAll(radare.getRegistersRead(address));
		registerNames.addAll(radare.getRegistersWritten(address));
		return registerNames;
	}

	private Vertex createAlocForRegister(String registerName) throws
			IOException
	{
		Vertex aloc;
		if (isFlag(registerName))
		{
			aloc = createFlagAloc(registerName);
		} else
		{
			aloc = createRegisterAloc(registerName);
			Vertex family = getRegisterFamilyNode(registerName);
			aloc.addEdge(BELONGS_TO_EDGE, family);
		}
		return aloc;
	}

	private Vertex getRegisterFamilyNode(String registerName)
	{
		String registerFamilyName = radare.getRegisterFamily(registerName);
		if (!registerFamilyCache.containsKey(registerFamilyName))
		{
			Vertex familyNode = GraphHelper.addVertex(graph, 0,
					BjoernNodeProperties.TYPE, FAMILY_TYPE,
					BjoernNodeProperties.NAME, registerFamilyName);
			registerFamilyCache
					.put(registerFamilyName, familyNode);
			return familyNode;
		}
		return registerFamilyCache.get(registerFamilyName);
	}

	private Vertex createRegisterAloc(String registerName)
	{
		return createAloc(registerName, AlocTypes.REGISTER,
				radare.getRegisterWidth(registerName));
	}

	private Vertex createFlagAloc(String flagName)
	{
		return createAloc(flagName, AlocTypes.FLAG, 1);
	}

	private Vertex createAloc(String alocName, String subType, Integer width)
	{
		return GraphHelper.addVertex(graph, 0,
				BjoernNodeProperties.TYPE, BjoernNodeTypes.ALOC,
				BjoernNodeProperties.SUBTYPE, subType,
				BjoernNodeProperties.NAME, alocName,
				BjoernNodeProperties.WIDTH, width);
	}

	private boolean isFlag(String registerName) throws IOException
	{
		Architecture architecture = radare.getArchitecture();
		return registerName.startsWith("$") || architecture
				.isFlag(registerName);
	}

}
