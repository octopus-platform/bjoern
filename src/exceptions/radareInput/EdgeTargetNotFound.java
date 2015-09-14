package exceptions.radareInput;

public class EdgeTargetNotFound extends Exception
{
	private boolean targetGiven;
	private long address;
	private String type;

	public EdgeTargetNotFound(boolean isTargetGiven, long addr, String aType)
	{
		targetGiven = isTargetGiven;
		address = addr;
		type = aType;
	}

	public long getAddress()
	{
		return address;
	}

	public boolean isTargetGiven()
	{
		return targetGiven;
	}

	public String getType()
	{
		return type;
	}

	private static final long serialVersionUID = 5274284964401464074L;

}
