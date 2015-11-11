package mobi.shush.goflick.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PersisterSerializable implements Persister<Serializable> {
	// private static PersisterSerializable instance;
	public static PersisterSerializable get( ) {
		// if(instance == null)
		// instance = new PersisterSerializable();
		// return instance;
		return new PersisterSerializable();
	}

	/**
	 * Write to a file the contents string
	 * 
	 * @param file
	 *            {@link File} to write to
	 * @param contents
	 *            {@link String} to write
	 * @return true if successful, false otherwise
	 */
	public boolean write(File file, Serializable contents) {
		try {
			FileOutputStream fos;
			ObjectOutputStream oos;
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(contents);
			oos.flush();
			fos.flush();
			oos.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * @param file
	 *            {@link File} to read
	 * @return {@link String} with the file contents, null otherwise
	 */
	public Serializable read(File file) {
		Serializable o = null;
		try {
			FileInputStream fis;
			ObjectInputStream ois;
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			o = (Serializable) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return o;
	}
}
