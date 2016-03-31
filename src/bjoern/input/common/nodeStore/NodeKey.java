package bjoern.input.common.nodeStore;

public class NodeKey {

	private String type = "";
	private Long address = 0l;
	private Integer counter = null;

	public NodeKey()
	{

	}

	public NodeKey(long addr)
	{
		setAddress(addr);
	}

	public NodeKey(long address, String type)
	{
		setAddress(address);
		setType(type);
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setAddress(Long address)
	{
		this.address = address;
	}

	@Override
	public String toString()
	{
		String key = this.type + "_" + this.address.toString();
		return (counter == null)? key : key + "_" + counter.toString();
	}

	public Long getAddress()
	{
		return this.address;
	}

}
