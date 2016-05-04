package bjoern.plugins.vsa.structures;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetOperationTests
{
	@Test
	public void testUnionNonOverlappingIntervals()
	{
		StridedInterval a = StridedInterval.getStridedInterval(16, 48, 1024, DataWidth.R64);
		StridedInterval b = StridedInterval.getStridedInterval(8, 4, 28, DataWidth.R64);

		StridedInterval expected = StridedInterval.getStridedInterval(4, 4, 1024, DataWidth.R64);

		assertEquals(expected, a.union(b));
	}

	@Test
	public void testUnionPartlyOverlappingIntervals()
	{
		StridedInterval a = StridedInterval.getStridedInterval(16, 48, 1024, DataWidth.R64);
		StridedInterval b = StridedInterval.getStridedInterval(8, 0, 256, DataWidth.R64);

		StridedInterval expected = StridedInterval.getStridedInterval(8, 0, 1024, DataWidth.R64);

		assertEquals(expected, a.union(b));
	}

	@Test
	public void testUnionFullyContainedInterval()
	{
		StridedInterval a = StridedInterval.getStridedInterval(16, 48, 1024, DataWidth.R64);
		StridedInterval b = StridedInterval.getStridedInterval(8, 98, 258, DataWidth.R64);

		StridedInterval expected = StridedInterval.getStridedInterval(2, 48, 1024, DataWidth.R64);

		assertEquals(expected, a.union(b));
	}

	@Test
	public void testUnionCommutativeProperty()
	{
		StridedInterval a = StridedInterval.getStridedInterval(16, 48, 1024, DataWidth.R64);
		StridedInterval b = StridedInterval.getStridedInterval(8, 98, 258, DataWidth.R64);

		assertEquals(a.union(b), b.union(a));
	}
}
