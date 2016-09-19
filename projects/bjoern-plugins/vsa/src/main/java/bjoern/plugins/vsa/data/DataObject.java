package bjoern.plugins.vsa.data;

public interface DataObject<T>
{
	String getIdentifier();

	DataObject<T> copy();

	T read();

	void write(T value);

}
