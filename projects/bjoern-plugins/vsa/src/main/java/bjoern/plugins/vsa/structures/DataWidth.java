package bjoern.plugins.vsa.structures;

public enum DataWidth
{
	R4(4),
	R64(64);

	private final int dataWidth;
	private final long minimumValue;
	private final long maximumValue;

	DataWidth(int dataWidth)
	{
		this.dataWidth = dataWidth;
		this.minimumValue = -(0x1l << (dataWidth - 1l));
		this.maximumValue = -(minimumValue + 1);
	}

	public long effectiveBits()
	{
		return 0x1 << (dataWidth - 1);
	}

	public long effectiveValue(long value)
	{
		long maskHighestBit = 0x1l << (dataWidth - 1l);
		return -(value & maskHighestBit) + (value & (maskHighestBit - 1l));
	}

	public int getDataWidth()
	{
		return dataWidth;
	}

	public long getMaximumValue()
	{
		return maximumValue;
	}

	public long getMinimumValue()
	{
		return minimumValue;
	}
}
