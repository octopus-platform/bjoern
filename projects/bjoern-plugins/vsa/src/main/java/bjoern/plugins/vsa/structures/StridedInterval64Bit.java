package bjoern.plugins.vsa.structures;

final class StridedInterval64Bit extends StridedInterval
{

	static final StridedInterval64Bit BOTTOM = new StridedInterval64Bit(1, 1, 0);
	static final StridedInterval64Bit TOP = new StridedInterval64Bit(1, DataWidth.R64.getMinimumValue(),
			DataWidth.R64.getMaximumValue());

	StridedInterval64Bit(long stride, long lower, long upper)
	{
		super(stride, lower, upper, DataWidth.R64);
	}

}
