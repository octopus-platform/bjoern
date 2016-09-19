package bjoern.plugins.vsa.data;

public interface DataObjectObserver<T>
{
	void updateRead(DataObject<T> dataObject);

	void updateWrite(DataObject<T> dataObject, T value);
}
