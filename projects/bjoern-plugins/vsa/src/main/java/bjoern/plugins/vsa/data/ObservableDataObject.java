package bjoern.plugins.vsa.data;

import java.util.ArrayList;
import java.util.Collection;

public class ObservableDataObject<T> implements DataObject<T>
{
	private final DataObject<T> dataObject;
	private Collection<DataObjectObserver<T>> observers;

	public ObservableDataObject(DataObject<T> dataObject)
	{
		this.dataObject = dataObject;
		this.observers = new ArrayList<>();
	}

	@Override
	public String getIdentifier()
	{
		return dataObject.getIdentifier();
	}

	@Override
	public ObservableDataObject<T> copy()
	{
		ObservableDataObject<T> copy = new ObservableDataObject<>(dataObject.copy());
		copy.observers.addAll(observers);
		return copy;
	}

	@Override
	public T read()
	{
		notifyAboutReadAccess();
		return dataObject.read();
	}

	@Override
	public void write(T value)
	{
		notifyAboutWriteAccess(value);
		dataObject.write(value);
	}

	@Override
	public String toString()
	{
		return dataObject.toString();
	}

	private void notifyAboutReadAccess()
	{
		for (DataObjectObserver<T> observer : observers)
		{
			observer.updateRead(dataObject);
		}
	}

	private void notifyAboutWriteAccess(T value)
	{
		for (DataObjectObserver<T> observer : observers)
		{
			observer.updateWrite(dataObject, value);
		}
	}

	public void addObserver(DataObjectObserver<T> observer)
	{
		this.observers.add(observer);
	}

}
