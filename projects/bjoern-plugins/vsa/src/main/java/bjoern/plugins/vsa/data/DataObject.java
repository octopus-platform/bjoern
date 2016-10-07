package bjoern.plugins.vsa.data;

/**
 * A object that can hold arbitrary data.
 */
public interface DataObject<T> {
	/**
	 * The identifier of this data object
	 *
	 * @return the identifier
	 */
	Object getIdentifier();

	/**
	 * Return a copy of this data object.
	 *
	 * @return a copy of this data object
	 */
	DataObject<T> copy();

	/**
	 * Read the data stored in this data object.
	 *
	 * @return the stored data
	 */
	T read();

	/**
	 * Write data to this data object.
	 *
	 * @param value
	 * 		the value to be stored
	 */
	void write(T value);

}
