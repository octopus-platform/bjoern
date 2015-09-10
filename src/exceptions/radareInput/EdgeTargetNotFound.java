package exceptions.radareInput;

public class EdgeTargetNotFound extends Exception
{
	private boolean targetGiven;
	private long address;

	public EdgeTargetNotFound(boolean isTargetGiven, long addr)
	{
		targetGiven = isTargetGiven;
		address = addr;
	}

	public long getAddress()
	{
		return address;
	}

	public boolean isTargetGiven()
	{
		return targetGiven;
	}

	private static final long serialVersionUID = 5274284964401464074L;

}
