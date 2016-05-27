package bjoern.plugins.vsa.structures;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitArithmeticTests
{

	@Test
	public void testNot1()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, 2, 4, DataWidth.R4);
		StridedInterval expected = StridedInterval.getStridedInterval(1, -5, -3, DataWidth.R4);
		assertEquals(expected, a.not());
	}

	@Test
	public void testNot2()
	{
		StridedInterval b = StridedInterval.getStridedInterval(2, -4, 4, DataWidth.R4);
		StridedInterval expected = StridedInterval.getStridedInterval(2, -5, 3, DataWidth.R4);
		assertEquals(expected, b.not());
	}

	@Test
	public void testOr1()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, 2, 4, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(2, -4, 4, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -4, 7, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr2()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr3()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -8, -4, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -4, -3, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -4, -1, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr4()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -7, 0, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, 3, 4, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -5, 4, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr5()
	{
		StridedInterval a = StridedInterval.getStridedInterval(2, -8, -6, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -8, -5, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr6()
	{
		StridedInterval a = StridedInterval.getStridedInterval(2, -7, -5, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -6, -5, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -5, -5, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr7()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, 1, 2, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -7, -5, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr8()
	{
		StridedInterval a = StridedInterval.getStridedInterval(4, -6, -2, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -3, -2, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -2, -1, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr9()
	{
		//TODO try to improve accuracy
//		StridedInterval a = StridedInterval.getStridedInterval(3, -8, 4, DataWidth.R4);
//		StridedInterval b = StridedInterval.getStridedInterval(1, 4, 5, DataWidth.R4);
//
//		StridedInterval expected = StridedInterval.getStridedInterval(1, -4, 5, DataWidth.R4);
//		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr10()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(1, -6, -5, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -6, -5, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}

	@Test
	public void testOr11()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, -8, -7, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(3, -6, -6, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, -6, -5, DataWidth.R4);
		assertEquals(expected, a.or(b));
	}


	@Test
	public void testAnd()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, 2, 4, DataWidth.R4);
		StridedInterval b = StridedInterval.getStridedInterval(2, -4, 4, DataWidth.R4);

		StridedInterval expected = StridedInterval.getStridedInterval(1, 0, 4, DataWidth.R4);
		assertEquals(expected, a.and(b));
	}

	@Test
	public void testAnd2()
	{
		StridedInterval a = StridedInterval.getStridedInterval(1, 0, 4, DataWidth.R4);
		StridedInterval b = StridedInterval.getTop(DataWidth.R4);

		assertEquals(a, a.and(b));
	}
}
