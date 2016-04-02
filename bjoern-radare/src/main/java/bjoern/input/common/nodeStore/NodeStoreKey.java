package bjoern.input.common.nodeStore;

public class NodeStoreKey
{
	String type;
	long address;

	public NodeStoreKey(long addr, String aType)
	{
		address = addr;
		type = aType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (address ^ (address >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeStoreKey other = (NodeStoreKey) obj;
		if (address != other.address)
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
