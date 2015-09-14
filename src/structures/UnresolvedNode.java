package structures;

import nodeStore.Node;

public class UnresolvedNode extends Node
{
	public UnresolvedNode(Long address, String type)
	{
		setAddr(address);
		setType(type);
	}

	public String getKey()
	{
		return getType() + "_" + getAddress().toString();
	}
}
