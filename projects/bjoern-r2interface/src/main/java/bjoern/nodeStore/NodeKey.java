package bjoern.nodeStore;

public final class NodeKey
{
	private final String type;
	private final Long address;

	public NodeKey(long address, String type)
	{
		this.address = address;
		this.type = type;
	}

	@Override
	public String toString()
	{
		return this.type + "_" + Long.toHexString(this.address);
	}

}
