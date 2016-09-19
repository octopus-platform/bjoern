package bjoern.plugins.vsa.data;

public abstract class GenericDataObject<T> implements DataObject<T>
{
	private final String identifier;
	private T value;

	public GenericDataObject(String identifier, T value)
	{
		this.identifier = identifier;
		this.value = value;
	}

	@Override
	public String getIdentifier()
	{
		return this.identifier;
	}

	@Override
	public T read()
	{
		return this.value;
	}

	@Override
	public void write(T value)
	{
		this.value = value;

	}

	@Override
	public String toString()
	{
		return this.identifier + " = " + this.value;
	}
}
