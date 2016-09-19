package bjoern.plugins.vsa.domain.memrgn;

public class GlobalRegion implements MemoryRegion
{
	private static final GlobalRegion GLOBAL_REGION = new GlobalRegion();

	private GlobalRegion() {}

	public static GlobalRegion getGlobalRegion()
	{
		return GLOBAL_REGION;
	}


	@Override
	public String toString()
	{
		return "global";
	}

}
