package bjoern.plugins.vsa.structures;

import java.math.BigInteger;

public class StridedInterval
{

	protected final long stride;
	protected final long lowerBound;
	protected final long upperBound;
	protected final DataWidth dataWidth;


	protected StridedInterval(long stride, long lower, long upper, DataWidth dataWidth)
	{
		// Strided intervals must be immutable

		lower = dataWidth.effectiveValue(lower);
		upper = dataWidth.effectiveValue(upper);

		if (stride < 0)
		{
			throw new IllegalArgumentException("Invalid strided interval: stride must not be negative");
		} else if (stride == 0 && lower != upper)
		{
			throw new IllegalArgumentException("Invalid strided interval: stride must not be zero");
		} else if ((lower > upper) && (stride != 1 || lower != 1 || upper != 0))
		{
			throw new IllegalArgumentException("Invalid strided interval: lower bound must not be larger than upper "
					+ "bound.");
		} else if (stride > 0 &&
				BigInteger.valueOf(upper).subtract(BigInteger.valueOf(lower)).mod(BigInteger.valueOf(stride))
						.longValue() != 0)
		{
			throw new IllegalArgumentException("Invalid strided interval: upper bound must be tight.");
		}

		this.stride = stride;
		this.lowerBound = lower;
		this.upperBound = upper;
		this.dataWidth = dataWidth;
	}

	public static StridedInterval getTop(DataWidth dataWidth)
	{
		switch (dataWidth.getWidth())
		{
			case 1:
				return StridedInterval1Bit.TOP;
			case 64:
				return StridedInterval64Bit.TOP;
			default:
				return getInterval(dataWidth.getMinimumValue(), dataWidth.getMaximumValue(), dataWidth);
		}
	}

	public static StridedInterval getBottom(DataWidth dataWidth)
	{
		// the bottom element (empty set) is always represented by a stride equal to 1, minimum value equal to 1, and
		// maximum value equal to 0.
		switch (dataWidth.getWidth())
		{
			case 1:
				return StridedInterval1Bit.BOTTOM;
			case 64:
				return StridedInterval64Bit.BOTTOM;
			default:
				return getStridedInterval(1, 1, 0, dataWidth);
		}
	}

	public static StridedInterval getSingletonSet(long number, DataWidth dataWidth)
	{
		// singleton sets have a stride equal to 0
		return getStridedInterval(0, number, number, dataWidth);
	}

	public static StridedInterval getInterval(long lower, long upper, DataWidth dataWidth)
	{
		return getStridedInterval(1, lower, upper, dataWidth);
	}

	public static StridedInterval getStridedInterval(long stride, long lower, long upper, DataWidth dataWidth)
	{
		switch (dataWidth.getWidth())
		{
			case 1:
				return new StridedInterval1Bit(stride, lower, upper);
			case 64:
				return new StridedInterval64Bit(stride, lower, upper);
			default:
				return new StridedInterval(stride, lower, upper, dataWidth);
		}
	}

	@Override
	public String toString()
	{
		if (isSingletonSet())
		{
			return "{" + lowerBound + "}";
		} else if (isBottom())
		{
			return "{}"; //empty set
		} else if (isInterval())
		{
			return "[" + lowerBound + ", " + upperBound + "]";
		} else
		{
			return stride + "[" + lowerBound + ", " + upperBound + "]";
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof StridedInterval))
		{
			return false;
		}
		StridedInterval other = (StridedInterval) o;
		return this.stride == other.stride && this.lowerBound == other.lowerBound
				&& this.upperBound == other.upperBound;
	}

	@Override
	public int hashCode()
	{
		int result = 17;
		result = 31 * result + (int) (stride ^ (stride >>> 32));
		result = 31 * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = 31 * result + (int) (upperBound ^ (upperBound >>> 32));
		return result;
	}

	public DataWidth getDataWidth()
	{
		return dataWidth;
	}

	public boolean isSingletonSet()
	{
		return stride == 0; // this implies that lowerBound == upperBound
	}

	public boolean isTop()
	{
		return this.equals(getTop(dataWidth));
	}

	public boolean isBottom()
	{
		return this.equals(getBottom(dataWidth));
	}

	public boolean isZero()
	{
		return isSingletonSet() && lowerBound == 0;
	}

	public boolean isOne()
	{
		return isSingletonSet() && lowerBound == 1;
	}

	public boolean isInterval()
	{
		return stride == 1;
	}

	public boolean contains(long number)
	{
		return !(lowerBound > number || upperBound < number) && (isSingletonSet() && lowerBound == number
				|| ((number - lowerBound) % stride) == 0);
	}

	public boolean contains(StridedInterval si)
	{
		if (isBottom())
		{
			return false;
		} else if (si.isBottom())
		{
			return true;
		} else if (isSingletonSet())
		{
			return equals(si);
		} else if (si.isSingletonSet())
		{
			return contains(si.lowerBound);
		} else
		{
			return si.stride % this.stride == 0
					&& si.lowerBound >= this.lowerBound && si.lowerBound <= this.upperBound
					&& si.upperBound >= this.lowerBound && si.upperBound <= this.upperBound
					&& this.contains(si.lowerBound);
		}
	}

	protected boolean isLowerBoundMinimal()
	{
		return this.lowerBound == dataWidth.getMinimumValue();
	}

	protected boolean isUpperBoundMaximal()
	{
		return this.upperBound == dataWidth.getMaximumValue();
	}

	protected long sharedSuffixMask()
	{
		if (isSingletonSet())
		{
			return 0xffffffffffffffffL;
		}
		int t = Long.numberOfTrailingZeros(stride);
		return (0x1 << t) - 1;
	}

	protected long sharedPrefixMask()
	{
		if (isSingletonSet())
		{
			return 0xffffffffffffffffL;
		}
		long l = Long.numberOfLeadingZeros(lowerBound ^ upperBound);
		if (l == 0)
		{
			return 0x0000000000000000L;
		}
		return 0x8000000000000000L >> (l - 1);
	}

	public StridedInterval add(long c)
	{
		return this.add(getSingletonSet(c, dataWidth));
	}

	public StridedInterval add(StridedInterval si)
	{
		if (dataWidth.compareTo(si.dataWidth) < 0)
		{
			return si.add(this);
		} else if (dataWidth.compareTo(si.dataWidth) > 0)
		{
			return add(si.signExtend(dataWidth));
		}

		long stride = gcd(this.stride, si.stride);
		long lower = this.lowerBound + si.lowerBound;
		long upper = this.upperBound + si.upperBound;

		long u = this.lowerBound & si.lowerBound & ~lower & ~(this.upperBound & si.upperBound & ~upper);
		long v = ((this.lowerBound ^ si.lowerBound) | ~(this.lowerBound ^ lower)) & (~this.upperBound & ~si.upperBound
				& upper);

		u = dataWidth.effectiveValue(u);
		v = dataWidth.effectiveValue(v);
		if ((u | v) < 0)
		{
			return getTop(dataWidth);
		} else
		{
			return getStridedInterval(stride, dataWidth.effectiveValue(lower), dataWidth.effectiveValue(upper),
					dataWidth);
		}
	}

	public StridedInterval sub(long c)
	{
		return this.add(-c);
	}

	public StridedInterval sub(StridedInterval si)
	{
		return this.add(si.negate());
	}

	public StridedInterval inc()
	{
		return this.add(1);
	}

	public StridedInterval dec()
	{
		return this.sub(1);
	}

	public StridedInterval union(StridedInterval si)
	{
		// handle inverted cases
		if (this.upperBound > si.upperBound)
		{
			return si.union(this);
		}

		// bounds are simple
		long lowerBound = Math.min(this.lowerBound, si.lowerBound);
		long upperBound = Math.max(this.upperBound, si.upperBound);
		long delta;

		if (this.upperBound <= si.lowerBound)
		{
			// non-overlapping intervals
			delta = si.lowerBound - this.upperBound;
		} else if (this.lowerBound < si.lowerBound && this.upperBound < si.upperBound)
		{
			// partly overlapping intervals
			delta = this.upperBound - si.lowerBound;
		} else //if (this.lowerBound >= si.lowerBound && this.upperBound <= si.upperBound)
		{
			// fully contained interval
			delta = this.lowerBound - si.lowerBound;
		}

		long gcd = gcd(gcd(this.stride, si.stride), delta);
		long stride = gcd < 0 ? -gcd : gcd;
		return getStridedInterval(stride, lowerBound, upperBound, DataWidth.maximum(dataWidth, si.dataWidth));
	}

	public StridedInterval intersect(StridedInterval si)
	{
		if (this.upperBound > si.upperBound)
		{
			return si.intersect(this);
		}

		if (this.upperBound <= si.lowerBound)
		{
			// non-overlapping intervals
			return getBottom(dataWidth);
		} else
		{
			// partly overlapping intervals
			long[] ans = extended_gcd(si.stride, this.stride);
			long gcd = ans[0];
			long u = ans[1];
			long v = ans[2];
			long difference = this.upperBound - si.lowerBound;
			long answerStride = (this.stride * si.stride) / gcd;

			// check if there can be any common elements
			if ((difference % gcd) != 0)
			{
				return getBottom(dataWidth);
			}

			// find one solution (let's call it the anchor) to
			// si.lowerBound + i0 * si.stride == this.upperbound - j0 * this.stride
			// Starting at the anchor we can jump to common points that reside in the bound of both intervals
			// by adding the stride of the answer strided interval
			long i0 = (difference / gcd) * u;
			long j0 = (difference / gcd) * v;
			long anchor = si.lowerBound + (i0 * si.stride);
			assert anchor == (this.upperBound - (j0 * this.stride)) : "Anchor is invalid";

			long t = (Math.max(this.lowerBound, si.lowerBound) - anchor) / answerStride;
			if (t >= 0 && ((Math.max(this.lowerBound, si.lowerBound) - anchor) % answerStride) != 0)
			{
				t++;
			}

			long answerLowerBound = anchor + (t * answerStride);
			if (answerLowerBound > Math.min(this.upperBound, si.upperBound))
			{
				return getBottom(dataWidth);
			} else if (Math.min(this.upperBound, si.upperBound) - answerLowerBound < answerStride)
			{
				return getSingletonSet(answerLowerBound, dataWidth);
			} else
			{
				long answerSize = (Math.min(this.upperBound, si.upperBound) - answerLowerBound) / answerStride + 1;
				return getStridedInterval(answerStride, answerLowerBound,
						answerLowerBound + (answerSize - 1) * answerStride, dataWidth);
			}
		}
	}

	public StridedInterval removeLowerBound()
	{
		if (lowerBound == dataWidth.getMinimumValue())
		{
			return this;
		} else if (isSingletonSet())
		{
			return getStridedInterval(1, dataWidth.getMinimumValue(), upperBound, dataWidth);
		} else
		{
			long lowerBound = dataWidth.getMinimumValue();
			long delta = BigInteger.valueOf(upperBound).subtract(BigInteger.valueOf(lowerBound))
					.mod(BigInteger.valueOf(stride)).longValue();
			lowerBound += delta;
			return getStridedInterval(stride, lowerBound, upperBound, dataWidth);
		}
	}

	public StridedInterval removeUpperBound()
	{
		if (upperBound == dataWidth.getMaximumValue())
		{
			return this;
		} else if (isSingletonSet())
		{
			return getStridedInterval(1, lowerBound, dataWidth.getMaximumValue(), dataWidth);
		} else
		{
			long upperBound = dataWidth.getMaximumValue();
			long delta = BigInteger.valueOf(upperBound).subtract(BigInteger.valueOf(lowerBound))
					.mod(BigInteger.valueOf(stride)).longValue();
			upperBound -= delta;
			return getStridedInterval(stride, lowerBound, upperBound, dataWidth);
		}
	}

	public StridedInterval negate()
	{
		if (isSingletonSet() && isLowerBoundMinimal())
		{
			return this;
		} else if (!isLowerBoundMinimal())
		{
			return getStridedInterval(stride, -upperBound, -lowerBound, dataWidth);
		} else
		{
			return getTop(dataWidth);
		}
	}

	public StridedInterval or(StridedInterval si)
	{
		if (dataWidth.compareTo(si.dataWidth) < 0)
		{
			return extend(si.dataWidth).or(si);
		} else if (dataWidth.compareTo(si.dataWidth) > 0)
		{
			return or(si.extend(dataWidth));
		}

		long suffixMask1 = this.sharedSuffixMask();
		long suffixMask2 = si.sharedSuffixMask();
		long suffixBits1 = suffixMask1 & this.lowerBound;
		long suffixBits2 = suffixMask2 & si.lowerBound;
		long suffixBits = suffixBits1 | suffixBits2;

		long t = Long.min(Long.numberOfTrailingZeros(this.stride), Long.numberOfTrailingZeros(si.stride));
		long mask = (0x1L << t) - 1;

		long answerStride = 0x1L << t;
		long answerSharedSuffix = (this.lowerBound & mask) | (si.lowerBound & mask);

		// zero out suffix bits
		long answerLowerBound =
				minOrSigned((this.lowerBound & ~mask), (this.upperBound & ~mask), (si.lowerBound & ~mask), (
						si.upperBound & ~mask));
		long answerUpperBound =
				maxOrSigned((this.lowerBound & ~mask), (this.upperBound & ~mask), (si.lowerBound & ~mask), (
						si.upperBound & ~mask));

		answerLowerBound = (answerLowerBound & ~mask) | answerSharedSuffix;
		answerUpperBound = (answerUpperBound & ~mask) | answerSharedSuffix;

		while ((answerLowerBound & suffixBits) != suffixBits) answerLowerBound++;
		while ((answerUpperBound & suffixBits) != suffixBits) answerUpperBound--;

		return getStridedInterval(answerStride, answerLowerBound, answerUpperBound, dataWidth);

	}

	public StridedInterval not()
	{
		return getStridedInterval(stride, ~upperBound, ~lowerBound, dataWidth);
	}

	public StridedInterval and(StridedInterval si)
	{
		return not().or(si.not()).not();
	}

	public StridedInterval xor(StridedInterval si)
	{
		return not().or(si).not().or(or(si.not()).not());
	}

	public Bool3 compare(StridedInterval si)
	{
		if (equals(si))
		{
			return Bool3.TRUE;
		} else
		{
			if (intersect(si).isBottom())
			{
				return Bool3.FALSE;
			} else
			{
				return Bool3.MAYBE;
			}
		}
	}

	public Bool3 smaller(StridedInterval si)
	{
		if (this.upperBound < si.lowerBound)
		{
			return Bool3.TRUE;
		} else if (this.lowerBound > si.upperBound)
		{
			return Bool3.FALSE;
		} else
		{
			return Bool3.MAYBE;
		}
	}


	public StridedInterval widen(StridedInterval stridedInterval)
	{
		StridedInterval answer = this;
		answer = lowerBound <= stridedInterval.lowerBound ? answer : answer.removeLowerBound();
		answer = upperBound >= stridedInterval.upperBound ? answer : answer.removeUpperBound();
		return answer;
	}

	public StridedInterval extend(DataWidth dataWidth)
	{
		if (this.dataWidth.compareTo(dataWidth) >= 0)
		{
			return this;
		} else
		{
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	public StridedInterval signExtend(DataWidth dataWidth)
	{
		if (this.dataWidth.compareTo(dataWidth) >= 0)
		{
			return this;
		} else
		{
			return getStridedInterval(stride, lowerBound, upperBound, dataWidth);
		}
	}

	public Bool3 greater(StridedInterval si)
	{
		return smaller(si).not();
	}

	public Bool3 greaterOrEqual(StridedInterval si)
	{
		return greater(si).or(compare(si));
	}

	public Bool3 smallerOrEqual(StridedInterval si)
	{
		return smaller(si).or(compare(si));
	}

	private static long gcd(long a, long b)
	{
		if (b == 0)
		{
			return a;
		}
		return gcd(b, a % b);
	}

	private static long[] extended_gcd(long a, long b)
	{
		if (b == 0)
		{
			return new long[]{a, 1, 0};
		}
		long[] ans = extended_gcd(b, a % b);
		return new long[]{ans[0], ans[2], ans[1] - ((a / b) * ans[2])};
	}

	private static long minOr(long a, long b, long c, long d)
	{
		// H.S. Warren, Jr. Hacker’s Delight. Addison-Wesley, 2003
		long m, temp;
		m = 0x8000000000000000L;
		while (m != 0)
		{
			if ((~a & c & m) != 0)
			{
				temp = (a | m) & -m;
				if (Long.compareUnsigned(temp, b) <= 0)
				{
					a = temp;
					break;
				}
			} else if ((a & ~c & m) != 0)
			{
				temp = (c | m) & -m;
				if (Long.compareUnsigned(temp, d) <= 0)
				{
					c = temp;
					break;
				}
			}
			m = m >>> 1;
		}
		return a | c;
	}

	private static long maxOr(long a, long b, long c, long d)
	{
		// H.S. Warren, Jr. Hacker’s Delight. Addison-Wesley, 2003
		long m, temp;
		m = 0x8000000000000000L;
		while (m != 0)
		{
			if ((b & d & m) != 0)
			{
				temp = (b - m) | (m - 1);
				if (Long.compareUnsigned(temp, a) >= 0)
				{
					b = temp;
					break;
				}
				temp = (d - m) | (m - 1);
				if (Long.compareUnsigned(temp, c) >= 0)
				{
					d = temp;
					break;
				}
			}
			m = m >>> 1;
		}
		return b | d;
	}

	private static long minOrSigned(long a, long b, long c, long d)
	{
		long m = 0x8000000000000000L;
		if ((a & b & c & d & m) != 0)
		{
			return minOr(a, b, c, d);
		} else if ((a & b & c & ~d & m) != 0)
		{
			return a;
		} else if ((a & b & ~c & ~d & m) != 0)
		{
			return minOr(a, b, c, d);
		} else if ((a & ~b & c & d & m) != 0)
		{
			return c;
		} else if ((a & ~b & c & ~d & m) != 0)
		{
			return Long.min(a, c);
		} else if ((a & ~b & ~c & ~d & m) != 0)
		{
			return minOr(a, -1, c, d);
		} else if ((~a & ~b & c & d & m) != 0)
		{
			return minOr(a, b, c, d);
		} else if ((~a & ~b & c & ~d & m) != 0)
		{
			return minOr(a, b, c, -1);
		} else if ((~a & ~b & ~c & ~d & m) != 0)
		{
			return minOr(a, b, c, d);
		} else
		{
			throw new IllegalArgumentException("Arguments are invalid bounds.");
		}
	}

	private static long maxOrSigned(long a, long b, long c, long d)
	{
		long m = 0x8000000000000000L;
		if ((a & b & c & d & m) != 0)
		{
			return maxOr(a, b, c, d);
		} else if ((a & b & c & ~d & m) != 0)
		{
			return -1;
		} else if ((a & b & ~c & ~d & m) != 0)
		{
			return maxOr(a, b, c, d);
		} else if ((a & ~b & c & d & m) != 0)
		{
			return -1;
		} else if ((a & ~b & c & ~d & m) != 0)
		{
			return maxOr(0, b, 0, d);
		} else if ((a & ~b & ~c & ~d & m) != 0)
		{
			return maxOr(0, b, c, d);
		} else if ((~a & ~b & c & d & m) != 0)
		{
			return maxOr(a, b, c, d);
		} else if ((~a & ~b & c & ~d & m) != 0)
		{
			return maxOr(a, b, 0, d);
		} else if ((~a & ~b & ~c & ~d & m) != 0)
		{
			return maxOr(a, b, c, d);
		} else
		{
			throw new IllegalArgumentException("Arguments are invalid bounds.");
		}
	}
}
