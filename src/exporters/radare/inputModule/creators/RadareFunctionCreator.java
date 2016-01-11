package exporters.radare.inputModule.creators;


import org.json.JSONObject;

import exporters.nodeStore.NodeKey;
import exporters.nodeStore.NodeTypes;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.edges.EdgeTypes;
import exporters.structures.interpretations.Function;


public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Function retval = new Function(jsonFunction.getLong("offset"));

		initFunctionInfo(jsonFunction, retval);

		return retval;
	}


	private static DirectedEdge createCallEdge(long dstAddr, long srcAddr)
	{

		DirectedEdge newEdge = new DirectedEdge();

		NodeKey dstKey = new NodeKey(srcAddr);
		NodeKey srcKey = new NodeKey(dstAddr);
		srcKey.setType(NodeTypes.INSTRUCTION);
		newEdge.setSourceKey(srcKey);
		newEdge.setDestKey(dstKey);
		newEdge.setType(EdgeTypes.CALL);

		return newEdge;
	}

	private static void initFunctionInfo(JSONObject jsonFunction,
			Function retval)
	{
		long addr = jsonFunction.getLong("offset");
		String name = jsonFunction.getString("name");
		retval.setAddr(addr);
		retval.setName(name);
	}

}
