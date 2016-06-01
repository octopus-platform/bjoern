package bjoern.plugins.vsa.structures;

import java.util.HashMap;
import java.util.Map;

public final class DataWidth implements Comparable<DataWidth>
{

	private static Map<Integer, DataWidth> cache;
	public static DataWidth R4;
	public static DataWidth R64;

	static
	{
		cache = new HashMap<>();
		R4 = getInstance(4);
		R64 = getInstance(64);
	}

	private final int width;
	private final long minimumValue;
	private final long maximumValue;

	private DataWidth(int width)
	{
		this.width = width;
		this.minimumValue = -(0x1l << (width - 1l));
		this.maximumValue = -(minimumValue + 1);
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

	public long effectiveValue(long value)
	{
		long highBitMask = 0x1l << (width - 1l);
		return -(value & highBitMask) + (value & (highBitMask - 1l));
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