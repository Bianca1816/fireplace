package com.fireplace.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FireDbHelper extends SQLiteOpenHelper {
	public static final String CREATE_APPLICATION_TABLE = "create table "
			+ Constants.APPS_TABLE_NAME + " (" 
			+ Constants.KEY_ID	+ " integer primary key autoincrement, "
			+ Constants.NAME + " text not null, "
			+ Constants.APP_LOC + " text not null, "
			+ Constants.CATEGORY + " integer not null default 1, "
			+ Constants.ICON_LOC + " text, "
			+ Constants.DESCRIPTION + " text, "
			+ Constants.DEVELOPER + " text, "
			+ Constants.STATUS + " integer default 1);";

	public FireDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("FireDbHelper onCreate", "Creating all the tables");
		try {
			db.execSQL(CREATE_APPLICATION_TABLE);
		} catch (SQLiteException ex) {
			Log.v("Create table exception", ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DbUpgrade", "Upgrading from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("drop table if exists " + Constants.APPS_TABLE_NAME);
		onCreate(db);
	}
}