package exporters.nodeStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Keeps track of nodes for different addresses, ensuring loosely that each node
 * is associated with exactly one address.
 */

public class NodeStore
{
	static HashMap<NodeStoreKey, Node> addrToNode = new HashMap<NodeStoreKey, Node>();

	public static void clearCache()
	{
		removeAllNodes();
	}

	/**
	 * Associate `node` with (address,type) pair. Raises an exception if a node
	 * is already registered for this pair.
	 */

	public static void addNode(Node node)
	{
		if (node == null)
			return;

		NodeStoreKey key = new NodeStoreKey(node.getAddress(), node.getType());

		if (addrToNode.get(key) != null)
			throw new RuntimeException("Duplicate node");

		addrToNode.put(key, node);
	}

	private static void removeAllNodes()
	{
		for (Iterator<Map.Entry<NodeStoreKey, Node>> it = addrToNode.entrySet()
				.iterator(); it.hasNext();)
		{
			Map.Entry<NodeStoreKey, Node> entry = it.next();
			Node node = entry.getValue();

			it.remove();
		}
	}

	/**
	 * Retrieve the node associated with the given address. Returns null if no
	 * such node exists.
	 */

	public static Node getNodeForAddressAndType(long address, String type)
	{
		return addrToNode.get(new NodeStoreKey(address, type));
	}
}
