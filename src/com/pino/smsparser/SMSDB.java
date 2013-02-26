package com.pino.smsparser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class SMSDB {
	private DBHelper DBH;
	private SQLiteDatabase DB;
	
	// constructor
	public SMSDB(Context context) {
		DBH = new DBHelper(context);
		DB = DBH.getWritableDatabase();
		Log.d("pino", Integer.toString(DB.getVersion()));		
	}
	
	class DBHelper extends SQLiteOpenHelper {
	    public DBHelper(Context context) {
	      // superclass constructor
	      super(context, "myDB", null, 1);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {	      
	      // create table
	      //Log.d("pino", "Going to create DB");
	      db.execSQL("create table sms_table ("
	          + "_id integer primary key autoincrement," 
	          + "date text,"
	          + "time text,"
	          + "type integer,"
	          + "money text" + ");");
	      
	      db.execSQL("insert into sms_table values (1,'','Total',2,0.0)");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	    	
	    	//Log.d("pino", "Going to update DB");
	    	db.execSQL("DROP TABLE IF EXISTS sms_table");
	    	onCreate(db);
	    }
	 }
	
	public boolean addTransaction(String date, String time, int type, double money) {
		ContentValues cv = new ContentValues();
		
		cv.put("date", date);
		cv.put("time", time);
		cv.put("type", type);
		cv.put("money", money);
		
		return (DB.insert("sms_table", null, cv) != -1) ? true : false; 
	}
	
	public Cursor getTransactions() {		
		return DB.query("sms_table", null, null, null, null, null, null);
	}
	
	public void flushDatabase() {
		DBH.onUpgrade(DB, 0, 1);
	}
}