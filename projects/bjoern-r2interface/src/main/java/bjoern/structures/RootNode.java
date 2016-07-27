package bjoern.structures;

public class RootNode extends Node
{

	public static class Builder extends Node.Builder
	{
		public Builder(Long address)
		{
			super(address, BjoernNodeTypes.ROOT);
		}

		public RootNode build()
		{
			return new RootNode(this);
		}
	}

	public RootNode(Builder builder)
	{
		super(builder);
	}

}
