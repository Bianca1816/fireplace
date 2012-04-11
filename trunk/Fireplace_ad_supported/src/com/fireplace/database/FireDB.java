package com.fireplace.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class FireDB {
	private SQLiteDatabase db;
	private final Context context;
	private final FireDbHelper dbhelper;
	private final String TAG = "FireDB";

	public FireDB(Context c) {
		context = c;
		dbhelper = new FireDbHelper(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
	}
	
	public void createProductTable(){
		try {
			db.execSQL(FireDbHelper.CREATE_APPLICATION_TABLE);
		} catch (SQLException e) {
			Log.e(TAG,"createProductTable", e);
		}
	}
	
	public void dropTable(String tableName){
		try {
			db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
		} catch (SQLException e) {
			Log.e(TAG,"dropTable " + tableName, e);
		}
	}
	
	public void commit(){
		try {
			db.execSQL("COMMIT;");
		} catch (SQLException e) {
			Log.e(TAG,"commit", e);
		}
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}

	public long insertApp(String name, String appLocation, int category,
			String iconLocation, String description, String developer, int status) {
		try {
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.NAME, name);
			newTaskValue.put(Constants.APP_LOC, appLocation);
			newTaskValue.put(Constants.CATEGORY, category);
			newTaskValue.put(Constants.ICON_LOC, iconLocation);
			newTaskValue.put(Constants.DESCRIPTION, description);
			newTaskValue.put(Constants.DEVELOPER, developer);
			newTaskValue.put(Constants.STATUS, status);
			return db.insert(Constants.APPS_TABLE_NAME, null, newTaskValue);
		} catch (SQLiteException ex) {
			Log.v("Insert into database exception caught", ex.getMessage());
			return -1;
		}
	}

	public Cursor getApps() {
		Cursor c = db.query(Constants.APPS_TABLE_NAME, null, null, null, null, null,
				null);
		return c;
	}
	
	public Cursor getAppsByCategory(int category) {
		String whereClause = Constants.CATEGORY + " = " + category;
		Cursor c = db.query(Constants.APPS_TABLE_NAME, null, whereClause, null, null, null,
				null);
		return c;
	}
}