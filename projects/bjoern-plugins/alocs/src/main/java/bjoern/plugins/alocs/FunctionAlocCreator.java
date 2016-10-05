package bjoern.plugins.alocs;

import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Function;
import bjoern.r2interface.Radare;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.BjoernNodeTypes;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.util.GraphHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionAlocCreator {
	private static final Logger logger = LoggerFactory
			.getLogger(FunctionAlocCreator.class);
	private static final String BELONGS_TO_EDGE = "BELONGS_TO";
	private static final String FAMILY_TYPE = "RegisterFamily";

	private Map<String, Vertex> registerFamilyCache = new HashMap<>();
	private Radare radare;
	private OrientGraphNoTx graph;

	FunctionAlocCreator(Radare radare, OrientGraphNoTx graph) throws
			IOException {
		this.radare = radare;
		this.graph = graph;
	}

	public void createAlocsForFunction(Function function) throws IOException {
		createAlocsForRegisters(function);
		createAlocsForLocalVariables(function);
	}

	public void createAlocsForRegisters(Function function) throws
			IOException {
		for (String registerName : getRegisterNames(function)) {
			try {
				Vertex aloc = createAlocForRegister(registerName);
				function.addEdge(Traversals.ALOC_USE_EDGE, aloc);
			} catch (IllegalArgumentException e) {
				logger.warn("Failed to create aloc for {}.", registerName);
			}
		}
	}

	public void createAlocsForLocalVariables(Function function) throws
			IOException {
		Long functionAddress = function.getAddress();
		JSONArray locals = radare.getBasePointerBasedVariables(
				functionAddress);
		for (int i = 0; i < locals.length(); i++) {
			JSONObject local = locals.getJSONObject(i);
			String name = local.getString("name");
			String base = local.getJSONObject("ref").getString("base");
			int offset = local.getJSONObject("ref").getInt("offset");
			Vertex aloc = createLocalAloc(name, base, offset);
			function.addEdge(Traversals.ALOC_USE_EDGE, aloc);
		}
	}

	private Set<String> getRegisterNames(Function function) throws
			IOException {
		Set<String> registerNames = radare.getRegistersUsedByFunctionAt(
				function.getAddress());
        registerNames.remove(radare.getRegisterByRole("SP"));
        registerNames.remove(radare.getRegisterByRole("PC"));
		return registerNames;
	}

	private Vertex createAlocForRegister(String registerName) throws
			IOException {
		Vertex aloc;
		if (isFlag(registerName)) {
			aloc = createFlagAloc(registerName);
		} else {
			aloc = createRegisterAloc(registerName);
			Vertex family = getRegisterFamilyNode(registerName);
			aloc.addEdge(BELONGS_TO_EDGE, family);
		}
		return aloc;
	}

	private Vertex getRegisterFamilyNode(String registerName) {
		String registerFamilyName = radare.getRegisterFamily(registerName);
		if (!registerFamilyCache.containsKey(registerFamilyName)) {
			Vertex familyNode = GraphHelper.addVertex(graph, 0,
					BjoernNodeProperties.TYPE, FAMILY_TYPE,
					BjoernNodeProperties.NAME, registerFamilyName);
			registerFamilyCache
					.put(registerFamilyName, familyNode);
			return familyNode;
		}
		return registerFamilyCache.get(registerFamilyName);
	}

	private Vertex createRegisterAloc(String registerName) {
		return createAloc(registerName, AlocTypes.REGISTER,
				radare.getRegisterWidth(registerName));
	}

	private Vertex createFlagAloc(String flagName) {
		return createAloc(flagName, AlocTypes.FLAG, 1);
	}

	private Vertex createLocalAloc(String name, String base, int offset) {
		Vertex aloc = createAloc(name, AlocTypes.LOCAL, -1);
		aloc.setProperty("base", base);
		aloc.setProperty("offset", offset);
		return aloc;
	}

	private Vertex createAloc(
			String alocName, String subType, Integer width) {
		return GraphHelper.addVertex(graph, 0,
				BjoernNodeProperties.TYPE, BjoernNodeTypes.ALOC,
				BjoernNodeProperties.SUBTYPE, subType,
				BjoernNodeProperties.NAME, alocName,
				BjoernNodeProperties.WIDTH, width);
	}

	private boolean isFlag(String registerName) throws IOException {
		return registerName.startsWith("$") || radare.isFlag(registerName);
	}

}
