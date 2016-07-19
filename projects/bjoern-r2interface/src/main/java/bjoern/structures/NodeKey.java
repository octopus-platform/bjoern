package bjoern.structures;

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
	public boolean equals(Object o)
	{
		if (!(o instanceof NodeKey))
		{
			return false;
		}
		NodeKey other = (NodeKey) o;

		return address.equals(other.address) && type.equals(other.type);
	}

	@Override
	public int hashCode()
	{
		int result = type.hashCode();
		result = 31 * result + address.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return this.type + "_" + Long.toHexString(this.address);
	}

}
