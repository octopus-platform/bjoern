package bjoern.plugins.vsa.structures;

public class StridedInterval1Bit extends StridedInterval
{
	static final StridedInterval1Bit BOTTOM = new StridedInterval1Bit(1, 1, 0);
	static final StridedInterval1Bit TOP = new StridedInterval1Bit(1, DataWidth.R64.getMinimumValue(),
			DataWidth.R1.getMaximumValue());

	protected StridedInterval1Bit(long stride, long lower, long upper)
	{
		super(stride, lower, upper, DataWidth.R1);
	}

	@Override
	public String toString()
	{
		if (isSingletonSet() || isBottom())
		{
			return super.toString();
		} else
		{
			return "{" + lowerBound + ", " + upperBound + "}";
		}
	}
}
