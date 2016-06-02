package bjoern.plugins.vsa.structures;

import java.util.HashMap;
import java.util.Map;

public final class DataWidth implements Comparable<DataWidth>
{

	private static Map<Integer, DataWidth> cache;
	public static DataWidth R1;
	public static DataWidth R4;
	public static DataWidth R64;

	static
	{
		cache = new HashMap<>();
		R1 = getInstance(1);
		R4 = getInstance(4);
		R64 = getInstance(64);
	}

	private final int width;
	private final long minimumValue;
	private final long maximumValue;

	private DataWidth(int width)
	{
		if (width < 1) {
			throw new IllegalArgumentException("Invalid width: width must be larger than zero.");
		}
		this.width = width;
		if (equals(R1)) {
			minimumValue = 0;
			maximumValue = 1;

		} else
		{
			minimumValue = -(0x1l << (width - 1l));
			maximumValue = -(minimumValue + 1);
		}
	}

	public static DataWidth getInstance(int width)
	{
		DataWidth o = cache.get(width);
		if (o == null)
		{
			o = new DataWidth(width);
			cache.put(width, o);
		}
		return o;
	}

	@Override
	public String toString()
	{
		return "R" + width;
	}

	public long effectiveValue(long value)
	{
		long highBitMask = 0x1l << (width - 1l);
		if (equals(R1)) {
			return value & highBitMask;
		} else
		{
			return -(value & highBitMask) + (value & (highBitMask - 1l));
		}
	}

	public int getWidth()
	{
		return width;
	}

	public long getMaximumValue()
	{
		return maximumValue;
	}

	public long getMinimumValue()
	{
		return minimumValue;
	}

	public int compareTo(DataWidth dataWidth)
	{
		return width - dataWidth.width;
	}

	public static DataWidth maximum(DataWidth dataWidth1, DataWidth dataWidth2)
	{
		if (dataWidth1.compareTo(dataWidth2) < 0)
		{
			return dataWidth2;
		} else
		{
			return dataWidth1;
		}
	}
}