package bjoern.plugins.vsa.structures;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArithmeticTests
{

	@Test
	public void testAddCase1()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -5, -3, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -7, -6, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, 4, 7, DataWidth.R4);
		assertEquals(expected, a.add(b));
	}

	@Test
	public void testAddCase2()
	{
		StridedInterval a = StridedInterval.getStridedInterval(6, -7, 5, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(2, -3, 7, DataWidth.R4);

		StridedInterval expexted = StridedInterval.getTop(DataWidth.R4);
		assertEquals(expexted, a.add(b));
	}

	@Test
	public void testAddCase3()
	{
		StridedInterval a = StridedInterval.getStridedInterval(4, -4, 4, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(2, -3, 3, DataWidth.R4);

		StridedInterval expexted = StridedInterval.getStridedInterval(2, -7, 7, DataWidth.R4);
		assertEquals(expexted, a.add(b));
	}

	@Test
	public void testNegateCase1()
	{
		StridedInterval a = StridedInterval.getSingletonSet(-8, DataWidth.R4);
		assertEquals(a, a.negate());
	}

	@Test
	public void testNegateCase2()
	{
		StridedInterval a = StridedInterval.getStridedInterval(3, -7, 5, DataWidth.R4);
		StridedInterval expected = StridedInterval.getStridedInterval(3, -5, 7, DataWidth.R4);
		assertEquals(expected, a.negate());
	}

	@Test
	public void testNegateCase3()
	{
		StridedInterval a = StridedInterval.getStridedInterval(3, -8, 4, DataWidth.R4);
		StridedInterval expected = StridedInterval.getTop(DataWidth.R4);
		assertEquals(expected, a.negate());
	}
}
