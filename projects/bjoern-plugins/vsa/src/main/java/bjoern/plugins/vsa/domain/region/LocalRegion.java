package bjoern.plugins.vsa.domain.region;

public class LocalRegion implements MemoryRegion
{
	private static int counter = 0;
	private final long id;

	private LocalRegion(long id)
	{
		this.id = id;
	}

	public static LocalRegion newLocalRegion()
	{
		return new LocalRegion(counter++);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof LocalRegion))
		{
			return false;
		}

		LocalRegion other = (LocalRegion) o;
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
		return "local" + id;
	}
}
