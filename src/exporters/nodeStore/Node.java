package exporters.nodeStore;

public class Node
{

	public static final Long INVALID_ADDRESS = Long.MAX_VALUE;

	private long address;
	private String type;

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

	public String getKey()
	{
		return getType() + "_" + getAddress().toString();
	}

}
