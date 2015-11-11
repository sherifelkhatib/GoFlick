package mobi.shush.goflick.persistance;

import java.io.File;

/**
 * Interface defining methods to load and save cache items to disk.
 * 
 * @author Sherif elKhatib
 * 
 * @param <T>
 *            type of cache item this Persister helps to load/save
 */
public interface Persister<T> {
	/**
	 * Saves a Cache item to File.
	 * 
	 * @param file
	 *            the {@link File} on which the cache item will be written
	 * @param contents
	 *            the cache item
	 * @return <i>true</i> if written, <i>false</i> otherwise
	 */
	boolean write(File file, T contents);

	/**
	 * Reads a Cache item from File.
	 * 
	 * @param file
	 *            the {@link File} from which the cache item will be read
	 * @return the cache item if read; <i>null</i> otherwise
	 */
	T read(File file);
}