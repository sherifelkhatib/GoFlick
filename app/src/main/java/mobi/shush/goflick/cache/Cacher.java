package mobi.shush.goflick.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import mobi.shush.goflick.persistance.Persister;
import mobi.shush.goflick.persistance.PersisterSerializable;
import mobi.shush.goflick.persistance.PersisterString;

/**
 * Class that manages caching. <br />
 * <br />
 * Example Usage: <br />
 * <code>
 * Cacher cache = Cacher.get();
 * <br />
 * cache.set("deviceid", "bla");
 * </code>
 * 
 * @author Sherif elKhatib
 * 
 */
public class Cacher {
	// private static final String LOGTAG = "foo-cacher";
	private static Cacher instance = null;

	/**
	 * Function that initializes a {@link Cacher} instance. <br />
	 * It is preferable to pass an application context as parameter using
	 * {@link Context#getApplicationContext()}.
	 * 
	 * @param context
	 *            {@link Context} to initialize the Cacher
	 * @return Cacher instance
	 */
	public static Cacher init(Context context) {
		if (instance == null) instance = new Cacher(context);
		return instance;
	}

	/**
	 * Function that gets the {@link Cacher} instance. <br />
	 * This function might return a null value if {@link Cacher#init()} has
	 * never been called.
	 * 
	 * @return Cacher instance
	 */
	public static Cacher get( ) {
		return instance;
	}

	private CacheTableAdapter sql;
	private Context mContext;

	private void init( ) {
		sql = new CacheTableAdapter(mContext);
	}

	/**
	 * Empties the whole cache <br />
	 * 
	 * @return number of deleted cached items
	 */
	public synchronized long empty( ) {
		ArrayList<Resource> resources = sql.getResources();
		for (int i = 0; i < resources.size(); i++ ) {
			Resource res = resources.get(i);
			File f = new File(res.getLocation());
			if (f.delete()) log("Deleted: " + res.getResource());
			else log("Unable to Delete:" + res.getResource());
		}
		return sql.clearCacheTable();
	}

	/**
	 * Deletes a certain cache item <br />
	 * 
	 * @param key
	 *            key of cache item to delete
	 * @return <i>true</i> if deleted, <i>false</i> otherwise <br />
	 *         Note, however, that this function will return false if there is
	 *         no cache item for this key
	 */
	public synchronized boolean delete(String key) {
		return sql.deleteResource(key) != 0;
	}

	/**
	 * Gets the cache item for a certain key. This function assumes that the
	 * cache item is of type {@link String}. <br />
	 * This is the same as calling: <br />
	 * <code>get(key, policy, PersisterString.get());</code> <br />
	 * 
	 * @param key
	 *            key of cache item to get
	 * @param policy
	 *            the {@link Policy} used to read this cache item
	 * @return the cache item if read; <i>null</i> otherwise.
	 */
	public String get(String key, Policy policy) {
		return get(key, policy, PersisterString.get());
	}

	/**
	 * Gets the cache item for a certain key. This function assumes that the
	 * cache item is of type {@link Serializable}. <br />
	 * This is the same as calling: <br />
	 * <code>get(key, policy, PersisterSerializable.get());</code> <br />
	 * 
	 * @param key
	 *            key of cache item to get
	 * @param policy
	 *            the {@link Policy} used to read this cache item
	 * @return the cache item if read; <i>null</i> otherwise.
	 */
	public Serializable getSerializable(String key, Policy policy) {
		return get(key, policy, PersisterSerializable.get());
	}

	/**
	 * Gets the cache item for a certain key. <br />
	 * 
	 * @param key
	 *            key of cache item to get
	 * @param policy
	 *            the {@link Policy} used to read this cache item
	 * @param persister
	 *            the {@link Persister} used to read the persisted data
	 * @return the cache item if read; <i>null</i> otherwise.
	 */
	public <T> T get(String key, Policy policy, Persister<T> persister) {
		log("Resource Requested: " + key);
		long time = System.currentTimeMillis();
		// getting resource from sql
		Resource res = sql.getResource(key);
		// if resource not found return null
		log("Resource " + ( ( res == null ) ? "not " : "" ) + "found: " + key);
		if (res == null) return null;
		// get the resource location from sql
		File f = new File(res.getLocation());
		// check if file exists
		if (f.exists()) {
			Policy.Result p = policy.read(res, time);
			T result = null;
			// if file exists return its content (if life allows) else delete
			// resource from sql
			if (p.canRead()) {
				log("Resource Content returned: " + key);
				result = persister.read(f);
			} else {
				log("Resource policy denied read: " + key);
			}
			if (p.shouldDelete()) {
				f.delete();
				sql.deleteResource(key);
			} else if (p.shouldUpdate()) {
				res.setLastAccess(time);
				sql.updateResource(res);
			}
			return result;
		} else {
			// if file does not exist delete it
			log("Deleting - Resource not found: " + key);
			sql.deleteResource(key);
			return null;
		}
	}

	/**
	 * Sets the cache item for a certain key. This function assumes that the
	 * cache item is of type {@link String}. <br />
	 * This is the same as calling: <br />
	 * <code>set(key, value, PersisterString.get());</code> <br />
	 * 
	 * @param key
	 *            key of cache item to set
	 * @param value
	 *            the cache item
	 * @return <i>true</i> if set, <i>false</i> otherwise
	 */
	public boolean set(String key, String value) {
		return set(key, value, PersisterString.get());
	}

	/**
	 * Sets the cache item for a certain key. This function assumes that the
	 * cache item is of type {@link Serializable}. <br />
	 * This is the same as calling: <br />
	 * <code>set(key, value, PersisterSerializable.get());</code> <br />
	 * 
	 * @param key
	 *            key of cache item to set
	 * @param value
	 *            the cache item
	 * @return <i>true</i> if set, <i>false</i> otherwise
	 */
	public boolean setSerializable(String key, Serializable value) {
		return set(key, value, PersisterSerializable.get());
	}

	/**
	 * Sets the cache item for a certain key. <br />
	 * 
	 * @param key
	 *            key of cache item to set
	 * @param value
	 *            the cache item
	 * @param persister
	 *            the {@link Persister} used to write the persisted data
	 * @return <i>true</i> if set, <i>false</i> otherwise
	 */
	public <T> boolean set(String key, T value, Persister<T> persister) {
		if (mContext == null) return false;
		// getting resource from sql
		Resource res = sql.getResource(key);
		log("Setting " + ( ( res == null ) ? "new" : "old" ) + " Resource: " + key);
		// if resource already there, get its file else get a new location
		File f = null;
		String filename;
		boolean resourceFound = ( res != null );
		if (resourceFound) {
			filename = res.getLocation();
		} else {
			filename = getFile(key);
		}
		// open the file we have
		f = new File(filename);
		if (resourceFound) {
			// if file exists and it is already owned by this resource update it
			if (persister.write(f, value)) {
				res = new Resource(key, filename, res);
				log("Overwriting old Resource succeeded: " + key);
				sql.updateResource(res);
			} else {
				log("Overwriting old Resource failed: " + key);
			}
		} else {
			String resultFile = filename;
			// if file exists but resource not alread there -i.e. not owning
			// this file, get a new file
			if (f.exists()) {
				log("Getting new File for Setting Resource: " + key);
				boolean notDone = true;
				int i = 0;
				do {
					resultFile = filename.concat(String.valueOf(i));
					f = new File(resultFile);
					notDone = f.exists();
					i++ ;
				} while (notDone);
			}
			if (persister.write(f, value)) {
				res = new Resource(key, resultFile, f.length(), 0, System.currentTimeMillis());
				log("Setting new Resource succeeded: " + key);
				sql.addResource(res);
			} else {
				log("Setting new Resource failed: " + key);
			}
		}
		return res != null;
	}

	private String getFile(String resource) {
		return mContext.getCacheDir() + MD5(resource);
	}

	private static String hash(String link) {
		// TODO
		link = link.replace("/", "l");
		link = link.replace(":", "+");
		link = link.replace("~", "s");
		return link;
	}

	private static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString( ( array[i] & 0xFF ) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException ex) {
			return hash(md5);
		}
	}

	private void log(String text) {
		// L.createD(LOGTAG).log(text);
	}

	protected Cacher(Context mContext) {
		this.mContext = mContext;
		init();
	}

	public static SharedPreferences of(Class<?> classIns) {
		return instance.mContext.getSharedPreferences(classIns.getName(), Context.MODE_PRIVATE); 
	}
}
