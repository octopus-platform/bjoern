package bjoern.plugins.vsa.domain.region;

public class HeapRegion implements MemoryRegion
{
	private static int counter = 0;
	private final long id;

	private HeapRegion(long id)
	{
		this.id = id;
	}

	public static HeapRegion getHeapRegion()
	{
		return new HeapRegion(counter++);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof HeapRegion))
		{
			return false;
		}

		HeapRegion other = (HeapRegion) o;
		return this.id == other.id;
	}

	@Override
	public int hashCode()
	{
		return (int) (id ^ (id >>> 32));
	}

	@Override
	public String toString()
	{
		return "heap" + id;
	}
}
