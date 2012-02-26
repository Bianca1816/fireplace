package com.fireplace.software;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteActivity {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_URL = "repository";
	
	private static final String DATABASE_NAME = "Repositorydb";
	private static final String DATABASE_TABLE = "repoTabel";
	private static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_URL + " TEXT NOT NULL);"				
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public SQLiteActivity (Context c) {
		ourContext = c;
	}
	
	public SQLiteActivity open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;		
	}
	
	public void close(){
		ourHelper.close();
	}

	public long createEntry(String repo) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_URL, repo);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}

	public String getData() {
		// TODO Auto-generated method stub
		String[] columns = new String[] {KEY_ROWID, KEY_URL};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		String result = "";
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iUrl = c.getColumnIndex(KEY_URL);
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			result = result + c.getString(iRow) + " " + c.getString(iUrl) + "\n"; 
		}
		
		return result;
	}

}
