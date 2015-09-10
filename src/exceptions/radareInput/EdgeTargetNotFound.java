package exceptions.radareInput;

public class EdgeTargetNotFound extends Exception
{
	private boolean addressExists;
	private long address;

	public EdgeTargetNotFound(boolean addrExists, long addr)
	{
		addressExists = addrExists;
		address = addr;
	}

	public long getAddress()
	{
		return address;
	}

	public boolean isAddressExists()
	{
		return addressExists;
	}

	private static final long serialVersionUID = 5274284964401464074L;

}
