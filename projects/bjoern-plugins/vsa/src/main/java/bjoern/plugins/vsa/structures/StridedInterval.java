package bjoern.plugins.vsa.structures;

public abstract class StridedInterval
{

	private final int stride;
	private final long lowerBound;
	private final long upperBound;

	StridedInterval(int stride, long lower, long upper)
	{
		if (stride < 0)
		{
			throw new IllegalArgumentException("Invalid strided interval: stride must not be negative");
		} else if (stride == 0 && lower != upper)
		{
			throw new IllegalArgumentException("Invalid strided interval: stride must not be negative");
		} else if ((lower > upper) && (stride != 1 || lower != 0 || upper != -1))
		{
			throw new IllegalArgumentException("Invalid strided interval: lower bound must not be larger than upper "
					+ "bound.");
		} else if (stride > 0 && ((upper - lower) % stride) != 0)
		{
			throw new IllegalArgumentException("Invalid strided interval: upper bound must be tight.");
		}
		this.stride = stride;
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	public static StridedInterval getTop(DataWidth dataWidth)
	{
		switch (dataWidth)
		{
			case R4:
				return StridedInterval4Bit.TOP;
			case R64:
				return StridedInterval64Bit.TOP;
			default:
				return StridedInterval64Bit.TOP;
		}
	}

	public static StridedInterval getBottom(DataWidth dataWidth)
	{
		switch (dataWidth)
		{
			case R4:
				return StridedInterval4Bit.BOTTOM;
			case R64:
				return StridedInterval64Bit.BOTTOM;
			default:
				return StridedInterval64Bit.BOTTOM;
		}
	}

	public static StridedInterval getSingletonSet(long number, DataWidth dataWidth)
	{
		switch (dataWidth)
		{
			case R4:
				return new StridedInterval4Bit(0, number, number);
			case R64:
			default:
				return new StridedInterval64Bit(0, number, number);
		}
	}

	public static StridedInterval getInterval(long lower, long upper, DataWidth dataWidth)
	{
		return getStridedInterval(1, lower, upper, dataWidth);
	}

	public static StridedInterval getStridedInterval(int stride, long lower, long upper, DataWidth dataWidth)
	{
		switch (dataWidth)
		{
			case R4:
				return new StridedInterval4Bit(stride, lower, upper);
			case R64:
			default:
				return new StridedInterval64Bit(stride, lower, upper);
		}
	}

	@Override
	public String toString()
	{
		return stride + "[" + lowerBound + ", " + upperBound + "]";
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
		result = 31 * result + stride;
		result = 31 * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = 31 * result + (int) (upperBound ^ (upperBound >>> 32));
		return result;
	}

	public boolean isSingletonSet()
	{
		return lowerBound == upperBound;
	}

	public boolean isTop()
	{
		return this.equals(getTop(this.getDataWidth()));
	}

	public boolean isBottom()
	{
		return this.equals(getBottom(this.getDataWidth()));
	}

	public boolean contains(long number)
	{
		if (lowerBound > number || upperBound < number)
		{
			return false;
		} else
		{
			return ((number - lowerBound) % stride) == 0;
		}
	}

	public boolean contains(StridedInterval si)
	{
		return si.stride % this.stride == 0
				&& si.lowerBound >= this.lowerBound && si.lowerBound <= this.upperBound
				&& si.upperBound >= this.lowerBound && si.upperBound <= this.upperBound
				&& this.contains(si.lowerBound);
	}

	private boolean isLowerBoundMinimal()
	{
		return this.lowerBound == lowerLimit();
	}

	private boolean isUpperBoundMaximal()
	{
		return this.upperBound == upperLimit();
	}

	public long get(int i)
	{
		if (i >= size() || i < 0)
		{
			throw new IllegalArgumentException("Invalid index.");
		}
		return lowerBound + i * stride;
	}

	public long size()
	{
		if (stride == 0)
		{
			return 1;
		}
		return 1 + ((upperBound - lowerBound) / stride);
	}

	public long sharedSuffixMask()
	{
		if (isSingletonSet())
		{
			return 0xffffffffffffffffl;
		}
		int t = Integer.numberOfTrailingZeros(stride);
		long mask = (0x1 << t) - 1;
		return mask;
	}

	public long sharedPrefixMask()
	{
		if (isSingletonSet())
		{
			return 0xffffffffffffffffl;
		}
		long l = Long.numberOfLeadingZeros(lowerBound ^ upperBound);
		if (l == 0)
		{
			return 0x0000000000000000l;
		}
		long mask = 0x8000000000000000l >> (l - 1);
		return mask;
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
		return this.add(-1);
	}

	public StridedInterval add(long c)
	{
		return this.add(getSingletonSet(c, getDataWidth()));
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

		int stride = (int) gcd(gcd(this.stride, si.stride), delta);
		return getStridedInterval(stride, lowerBound, upperBound, getDataWidth());
	}

	public StridedInterval intersect(StridedInterval si)
	{
		if (si.getDataWidth() != this.getDataWidth())
		{
			throw new IllegalArgumentException("Incompatible width.");
		}

		// handle inverted cases
		if (this.upperBound > si.upperBound)
		{
			return si.intersect(this);
		}

		if (this.upperBound <= si.lowerBound)
		{
			// non-overlapping intervals
			return getBottom(this.getDataWidth());
		} else
		{
			// partly overlapping intervals
			long[] ans = extended_gcd(si.stride, this.stride);
			int gcd = (int) ans[0];
			long u = ans[1];
			long v = ans[2];
			long difference = this.upperBound - si.lowerBound;
			int answerStride = (this.stride * si.stride) / gcd;

			// check if there can be any common elements
			if ((difference % gcd) != 0)
			{
				return getBottom(this.getDataWidth());
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
				return getBottom(getDataWidth());
			} else if (Math.min(this.upperBound, si.upperBound) - answerLowerBound < answerStride)
			{
				return getSingletonSet(answerLowerBound, getDataWidth());
			} else
			{
				long answerSize = (Math.min(this.upperBound, si.upperBound) - answerLowerBound) / answerStride + 1;
				return getStridedInterval(answerStride, answerLowerBound,
						answerLowerBound + (answerSize - 1) * answerStride, getDataWidth());
			}
		}
	}

	public StridedInterval removeLowerBound()
	{
		return getStridedInterval(stride, lowerLimit(), upperBound, getDataWidth());
	}

	public StridedInterval removeUpperBound()
	{
		return getStridedInterval(stride, lowerBound, upperLimit(), getDataWidth());
	}

	public StridedInterval add(StridedInterval si)
	{
		if (si.getDataWidth() != this.getDataWidth())
		{
			throw new IllegalArgumentException("Incompatible width.");
		}

		int stride = (int) gcd(this.stride, si.stride);
		long lower = this.lowerBound + si.lowerBound;
		long upper = this.upperBound + si.upperBound;

		long u = this.lowerBound & si.lowerBound & ~lower & ~(this.upperBound & si.upperBound & ~upper);
		long v = ((this.lowerBound ^ si.lowerBound) | ~(this.lowerBound ^ lower)) & (~this.upperBound & ~si.upperBound
				& upper);
		u = getDataWidth().effectiveValue(u);
		v = getDataWidth().effectiveValue(v);
		if ((u | v) < 0)
		{
			return getTop(getDataWidth());
		} else
		{
			return getStridedInterval(stride, getDataWidth().effectiveValue(lower), getDataWidth().effectiveValue
					(upper), getDataWidth());
		}
	}

	public StridedInterval negate()
	{
		if (isSingletonSet() && isLowerBoundMinimal())
		{
			return this;
		} else if (!isLowerBoundMinimal())
		{
			return getStridedInterval(stride, -upperBound, -lowerBound, getDataWidth());
		} else
		{
			return getTop(getDataWidth());
		}
	}

	public StridedInterval or(StridedInterval si)
	{
		if (si.getDataWidth() != this.getDataWidth())
		{
			throw new IllegalArgumentException("Incompatible widths.");
		}

		long suffixMask1 = this.sharedSuffixMask();
		long suffixMask2 = si.sharedSuffixMask();
		long suffixBits1 = suffixMask1 & this.lowerBound;
		long suffixBits2 = suffixMask2 & si.lowerBound;
		long suffixBits = suffixBits1 | suffixBits2;

		long t = Long.min(Integer.numberOfTrailingZeros(this.stride), Integer.numberOfTrailingZeros(si.stride));
		long mask = (0x1 << t) - 1;

		int answerStride = 0x1 << t;
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

		return getStridedInterval(answerStride, answerLowerBound, answerUpperBound, this.getDataWidth());

	}

	public StridedInterval not()
	{
		return getStridedInterval(stride, ~upperBound, ~lowerBound, getDataWidth());
	}

	public StridedInterval and(StridedInterval si)
	{
		return this.not().or(si.not()).not();
	}

	public StridedInterval xor(StridedInterval si)
	{
		return this.not().or(si).not().or(this.or(si.not()).not());
	}

	public Bool3 compare(StridedInterval si)
	{
		if (this.equals(si))
		{
			return Bool3.TRUE;
		} else
		{
			if (this.intersect(si).isBottom())
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

	public abstract DataWidth getDataWidth();

	public long upperLimit()
	{
		return getDataWidth().getMaximumValue();
	}

	public long lowerLimit()
	{
		return getDataWidth().getMinimumValue();
	}

	private static long gcd(long a, long b)
	{
		if (b == 0)
		{
			return a;
		}
		return gcd(b, a % b);
	}

	public static long[] extended_gcd(long a, long b)
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
		m = 0x8000000000000000l;
		m = 0x80;
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
		m = 0x8000000000000000l;
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
		long m = 0x8000000000000000l;
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
		long m = 0x8000000000000000l;
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
