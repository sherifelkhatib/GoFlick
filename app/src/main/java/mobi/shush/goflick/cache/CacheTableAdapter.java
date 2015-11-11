package mobi.shush.goflick.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class CacheTableAdapter extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "foocache";
	private static final int DATABASE_VERSION = 1;

	public CacheTableAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		CacheTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		CacheTable.onUpgrade(database, oldVersion, newVersion);
	}

	public long addResource(Resource resource) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = createResourceValues(resource);
		return db.insertOrThrow(CacheTable.TNAME, null, values);
	}

	public long updateResource(Resource resource) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = createResourceValues(resource);
		return db.update(CacheTable.TNAME, values, CacheTable.col.RESOURCE + "=" + "?", new String[]{resource.getResource()});
	}

	public long deleteResource(String resource) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(CacheTable.TNAME, CacheTable.col.RESOURCE + "=" + "?", new String[]{resource});
	}

	private ContentValues createResourceValues(Resource resource) {
		ContentValues values = new ContentValues();
		values.put(CacheTable.col.RESOURCE, resource.getResource());
		values.put(CacheTable.col.DATECREATED, String.valueOf(resource.getDateCreated()));
		values.put(CacheTable.col.LOCATION, resource.getLocation());
		values.put(CacheTable.col.LASTACCESS, String.valueOf(resource.getLastAccess()));
		values.put(CacheTable.col.SIZE, String.valueOf(resource.getSize()));
		return values;
	}

	public Resource[] fetchAllCachedResources( ) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor mCursor = db.query(CacheTable.TNAME, new String[]{CacheTable.col.RESOURCE, CacheTable.col.LOCATION, CacheTable.col.SIZE, CacheTable.col.LASTACCESS, CacheTable.col.DATECREATED}, null, null, null, null, null);
		ArrayList<Resource> result = new ArrayList<Resource>();
		if (mCursor.moveToFirst()) {
			do {
				result.add(new Resource(mCursor.getString(mCursor.getColumnIndex(CacheTable.col.RESOURCE)), mCursor.getString(mCursor.getColumnIndex(CacheTable.col.LOCATION)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.SIZE)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.LASTACCESS)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.DATECREATED))));
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		Resource resources[] = new Resource[result.size()];
		result.toArray(resources);
		return resources;
	}

	public Resource getResource(String resource) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor mCursor = db.query(CacheTable.TNAME, new String[]{CacheTable.col.RESOURCE, CacheTable.col.LOCATION, CacheTable.col.SIZE, CacheTable.col.LASTACCESS, CacheTable.col.DATECREATED}, CacheTable.col.RESOURCE + "=" + "?", new String[]{resource}, null, null, null);
			Resource result = null;
			if (mCursor.moveToFirst()) result = new Resource(mCursor.getString(mCursor.getColumnIndex(CacheTable.col.RESOURCE)), mCursor.getString(mCursor.getColumnIndex(CacheTable.col.LOCATION)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.SIZE)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.LASTACCESS)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.DATECREATED)));
			mCursor.close();
			return result;
		} catch (Exception ex) {
			return null;
		}
	}

	public long getTotalSize( ) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor mCursor = db.query(CacheTable.TNAME, new String[]{"SUM(" + CacheTable.col.SIZE + ")"}, null, null, null, null, null);
			if (mCursor.moveToFirst()) return mCursor.getLong(mCursor.getColumnIndex("SUM(" + CacheTable.col.SIZE + ")"));
			return 0;
		} catch (Exception ex) {
			return 0;
		}
	}

	public ArrayList<Resource> getResources( ) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor mCursor = db.query(CacheTable.TNAME, new String[]{CacheTable.col.RESOURCE, CacheTable.col.LOCATION, CacheTable.col.SIZE, CacheTable.col.LASTACCESS, CacheTable.col.DATECREATED}, null, null, null, null, null);
			ArrayList<Resource> result = new ArrayList<Resource>();
			if (mCursor.moveToFirst()) do {
				result.add(new Resource(mCursor.getString(mCursor.getColumnIndex(CacheTable.col.RESOURCE)), mCursor.getString(mCursor.getColumnIndex(CacheTable.col.LOCATION)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.SIZE)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.LASTACCESS)), mCursor.getLong(mCursor.getColumnIndex(CacheTable.col.DATECREATED))));
			} while (mCursor.moveToNext());
			mCursor.close();
			return result;
		} catch (Exception ex) {
			return null;
		}
	}

	public long clearCacheTable( ) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			return db.delete(CacheTable.TNAME, "1", null);
		} catch (Exception ex) {
			return 0;
		}
	}
}