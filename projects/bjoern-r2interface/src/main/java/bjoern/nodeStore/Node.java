package bjoern.nodeStore;

public class Node
{

	public static final Long INVALID_ADDRESS = Long.MAX_VALUE;

	private long address;
	private String type = "";

	public void setAddr(long addr)
	{
		address = addr;
	}

	public Long getAddress()
	{
		return address;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public NodeKey createKey()
	{
		NodeKey key = new NodeKey();
		key.setType(getType());
		key.setAddress(getAddress());
		return key;
	}

	public NodeKey createEpsilonKey()
	{
		NodeKey key = new NodeKey();
		key.setType(NodeTypes.ROOT);
		key.setAddress(getAddress());
		return key;
	}

	public String getKey()
	{
		return getType() + "_" + getAddress().toString();
	}

}
