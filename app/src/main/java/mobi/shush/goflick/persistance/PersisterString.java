package mobi.shush.goflick.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersisterString implements Persister<String> {
	// private static PersisterString instance;
	public static PersisterString get( ) {
		// if(instance == null)
		// instance = new PersisterString();
		// return instance;
		return new PersisterString();
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
	public boolean write(File file, String contents) {
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
	public String read(File file) {
		String o = null;
		try {
			FileInputStream fis;
			ObjectInputStream ois;
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			o = (String) ois.readObject();
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
