package bjoern.plugins.vsa.structures;

final class StridedInterval64Bit extends StridedInterval
{

	static final StridedInterval64Bit BOTTOM = new StridedInterval64Bit(1, 0, -1);
	static final StridedInterval64Bit TOP = new StridedInterval64Bit(1, DataWidth.R64.getMinimumValue(),
			DataWidth.R64.getMaximumValue());

	StridedInterval64Bit(int stride, long lower, long upper)
	{
		super(stride, lower, upper);
	}

	@Override
	public DataWidth getDataWidth()
	{
		return DataWidth.R64;
	}

}
