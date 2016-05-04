package bjoern.plugins.vsa.structures;

final class StridedInterval4Bit extends StridedInterval
{
	static final StridedInterval4Bit BOTTOM = new StridedInterval4Bit(1, 0, -1);
	static final StridedInterval4Bit TOP = new StridedInterval4Bit(1, DataWidth.R4.getMinimumValue(),
			DataWidth.R4.getMaximumValue());

	StridedInterval4Bit(int stride, long lower, long upper)
	{
		super(stride, lower, upper);
	}

	@Override
	public DataWidth getDataWidth()
	{
		return DataWidth.R4;
	}
}
