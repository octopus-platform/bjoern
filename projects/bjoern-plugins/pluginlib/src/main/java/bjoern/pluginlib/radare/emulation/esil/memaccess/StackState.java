package bjoern.pluginlib.radare.emulation.esil.memaccess;

public class StackState {

	private long basePtrValue;
	private long stackPtrValue;

	public StackState(long basePtrValue, long stackPtrValue)
	{
		this.basePtrValue = basePtrValue;
		this.stackPtrValue = stackPtrValue;
	}

	public long getBasePtrValue()
	{
		return basePtrValue;
	}

	public long getStackPtrValue()
	{
		return stackPtrValue;
	}

}
