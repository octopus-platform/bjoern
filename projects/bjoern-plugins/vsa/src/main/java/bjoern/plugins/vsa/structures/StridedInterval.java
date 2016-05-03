package bjoern.plugins.vsa.structures;

public abstract class StridedInterval
{

	private final int stride;
	private final long lowerBound;
	private final long upperBound;

	StridedInterval(int stride, long lower, long upper)
	{

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
		throw new UnsupportedOperationException("Not yet implemented.");
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

		long u = this.lowerBound & si.lowerBound & ~lower
				& ~(this.upperBound & si.upperBound & ~upper);
		long v = ((this.lowerBound ^ si.lowerBound) | ~(this.lowerBound ^ lower))
				& (~this.upperBound & ~si.upperBound & upper);
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

		long t = Long.min(Integer.numberOfTrailingZeros(this.stride),
				Integer.numberOfTrailingZeros(si.stride));
		long mask = (0x1 << t) - 1;

		int answerStride = 0x1 << t;
		long answerSharedSuffix = (this.lowerBound & mask) | (si.lowerBound & mask);

		// zero out suffix bits
		long answerLowerBound = minOrSigned((this.lowerBound & ~mask), (this.upperBound & ~mask),
				(si.lowerBound & ~mask), (si.upperBound & ~mask));
		long answerUpperBound = maxOrSigned((this.lowerBound & ~mask), (this.upperBound & ~mask),
				(si.lowerBound & ~mask), (si.upperBound & ~mask));

		answerLowerBound = (answerLowerBound & ~mask) | answerSharedSuffix;
		answerUpperBound = (answerUpperBound & ~mask) | answerSharedSuffix;

		while ((answerLowerBound & suffixBits) != suffixBits)
			answerLowerBound++;
		while ((answerUpperBound & suffixBits) != suffixBits)
			answerUpperBound--;

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

	public StridedInterval intersect(StridedInterval si)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
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
