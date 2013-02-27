package com.pino.smsparser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class SMSDB {
	private DBHelper DBH;
	private SQLiteDatabase DB;
	
	// row types
	public static final int TR_INCR = 1;
	public static final int TR_DECR = 0;
	public static final int TR_BASE = 2;
	public static final int TR_TOTL = 3;
	
	// constructor
	public SMSDB(Context context) {
		DBH = new DBHelper(context);
		DB = DBH.getWritableDatabase();	
	}
	
	class DBHelper extends SQLiteOpenHelper {
	    public DBHelper(Context context) {
	      // superclass constructor
	      super(context, "myDB", null, 1);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {	      
	      // create table
	      db.execSQL("create table sms_table ("
	          + "_id integer primary key autoincrement," 
	          + "date text,"
	          + "time text,"
	          + "type integer,"
	          + "money text" + ");");
	      
	      // add a row for a baseline
	      db.execSQL("insert into sms_table values (1,'','Base',"+Integer.toString(TR_BASE)+",0.0)");
	      
	      // add a row for a total
	      db.execSQL("insert into sms_table values (2,'','Total',"+Integer.toString(TR_TOTL)+",0.0)");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	db.execSQL("DROP TABLE IF EXISTS sms_table");
	    	onCreate(db);
	    }
	 }
	
	public void addTransaction(String date, String time, int type, double money) {
		ContentValues cv = new ContentValues();
		
		cv.put("date", date);
		cv.put("time", time);
		cv.put("type", type);
		cv.put("money", money);
		
		// fire in the hole
		DB.beginTransaction();
		
		// read current total value
		Cursor c = DB.query("sms_table", new String[] {"money"}, "type="+Integer.toString(TR_TOTL), null, null, null, null);
		c.moveToFirst();
		double m = c.getDouble(0);
		
		// update its row
		DB.update("sms_table", cv, "type="+Integer.toString(TR_TOTL), null);
		
		// insert a new total on top		
		cv.clear();
		cv.put("date", "");
		cv.put("time", "Total");
		cv.put("type", 3);
		cv.put("money", m);
		
		DB.insert("sms_table", null, cv);
		
		// lets assume all went right
		DB.setTransactionSuccessful();
		DB.endTransaction();
		
		updateTotal();
	}
	
	public Cursor getTransactions() {		
		return DB.query("sms_table", null, null, null, null, null, "_id desc");
	}
	
	public void flushDatabase() {
		DBH.onUpgrade(DB, 0, 1);
	}
	
	public void setBaseline(double base) {
		DB.execSQL("update sms_table set money=" + Double.toString(base) + " where _id=1");
		
		updateTotal();
	}
	
	private void updateTotal() {
		double sum = 0.0;
		
		Cursor c = getTransactions();
		c.moveToFirst();
		
		do {
			if (c.getInt(3) == TR_INCR || c.getInt(3) == TR_BASE) {
				sum += c.getDouble(4);
			}
			else if (c.getInt(3) == TR_DECR) {
				sum -= c.getDouble(4);
			}
		} while (c.moveToNext());
		
		DB.execSQL("update sms_table set money=" + Double.toString(sum) + " where type="+Integer.toString(TR_TOTL));
	}
}
