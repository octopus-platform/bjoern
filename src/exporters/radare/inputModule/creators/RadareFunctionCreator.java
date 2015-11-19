package exporters.radare.inputModule.creators;


import org.json.JSONArray;
import org.json.JSONObject;

import exporters.nodeStore.Node;
import exporters.structures.BasicBlock;
import exporters.structures.Function;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.edges.EdgeTypes;


public class RadareFunctionCreator
{

	public static Function createFromJSON(JSONObject jsonFunction)
	{
		Function retval = new Function();

		initFunctionInfo(jsonFunction, retval);
		initReferences(jsonFunction, retval);

		return retval;
	}

	private static void initReferences(JSONObject jsonFunction, Function retval)
	{
		long dstAddr = retval.getAddress();

		JSONArray callRefArray = jsonFunction.getJSONArray("callrefs");
		int nCallRefs = callRefArray.length();
		for (int i = 0; i < nCallRefs; i++)
		{
			JSONObject callRef = callRefArray.getJSONObject(i);
			long srcAddr = callRef.getLong("addr");
			DirectedEdge newEdge = createCallEdge(dstAddr, srcAddr);
			retval.addUnresolvedEdge(newEdge);
		}

	}

	private static DirectedEdge createCallEdge(long dstAddr, long srcAddr)
	{
		Node srcNode = new BasicBlock();
		srcNode.setAddr(srcAddr);

		Node dstNode = new BasicBlock();
		dstNode.setAddr(dstAddr);

		DirectedEdge newEdge = new DirectedEdge();
		newEdge.setSourceNode(srcNode);
		newEdge.setDestNode(dstNode);
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
