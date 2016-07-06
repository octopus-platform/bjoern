package bjoern.nodeStore;

public abstract class Node
{
	private long address;
	private String type;

	public Node(long address, String type)
	{
		setAddr(address);
		setType(type);
	}

	private void setAddr(long addr)
	{
		address = addr;
	}

	public Long getAddress()
	{
		return address;
	}

	public String getAddressAsHexString()
	{
		return Long.toHexString(getAddress());
	}

	public String getType()
	{
		return type;
	}

	private void setType(String type)
	{
		this.type = type;
	}

	public NodeKey createKey()
	{
		NodeKey key = new NodeKey(getAddress(), getType());
		return key;
	}

	public NodeKey createEpsilonKey()
	{
		NodeKey key = new NodeKey(getAddress(), NodeTypes.ROOT);
		return key;
	}

	public String getKey()
	{
		return getType() + "_" + getAddressAsHexString();
	}

}
