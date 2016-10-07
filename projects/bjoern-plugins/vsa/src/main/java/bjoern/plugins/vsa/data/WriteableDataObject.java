package bjoern.plugins.vsa.data;

public abstract class WriteableDataObject<T> implements DataObject<T> {
	private final Object identifier;
	private T value;

	public WriteableDataObject(Object identifier, T value) {
		this.identifier = identifier;
		this.value = value;
	}

	@Override
	public Object getIdentifier() {
		return this.identifier;
	}

	@Override
	public T read() {
		return this.value;
	}

	@Override
	public void write(T value) {
		this.value = value;

	}

	@Override
	public String toString() {
		return this.identifier + " = " + this.value;
	}
}
