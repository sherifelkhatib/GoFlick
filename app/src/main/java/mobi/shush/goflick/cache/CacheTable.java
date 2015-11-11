package mobi.shush.goflick.cache;

import android.database.sqlite.SQLiteDatabase;

class CacheTable {
	public static class col {
		public static final String RESOURCE = "resource";
		public static final String DATECREATED = "life";
		public static final String LOCATION = "location";
		public static final String LASTACCESS = "lastaccess";
		public static final String SIZE = "size";
	}

	public static final String TNAME = "cache";
	private static final String DATABASE_CREATE = "create table " + TNAME + " " + "(" + col.RESOURCE + " text primary key not null" + ", " + col.DATECREATED + " text not null" + ", " + col.LASTACCESS + " text not null" + ", " + col.SIZE + " text not null" + ", " + col.LOCATION + " text not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TNAME);
		onCreate(database);
	}
}