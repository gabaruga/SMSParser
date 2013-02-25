package com.pino.smsparser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {
	public static ListView list;
	public static SMSDB smsdb;
	private static Context CNTXT;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CNTXT = this;		
		list = (ListView)findViewById(R.id.list);		
		smsdb = new SMSDB(CNTXT);		
		updateList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static void updateList() {
			Cursor cur = smsdb.getTransactions();
			
			Log.d("pino", Integer.toString(cur.getCount()));
			
			String[] from = {"date", "time", "type", "money"};
			int[] to = {R.id.date, R.id.time, R.id.holder, R.id.money};
			
			SimpleCursorAdapter a = new SimpleCursorAdapter(CNTXT, R.layout.sms_holder, cur, from, to, 0);	
			
			a.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
					if (arg0.getId() == R.id.holder) {
						if (arg1.getInt(arg2) == 0) {
							arg0.setBackgroundColor(0xffCC6666);							
						}
						else if (arg1.getInt(arg2) == 1) {
							arg0.setBackgroundColor(0xff99CC66);							
						}
						else {
							arg0.setBackgroundColor(0xff000000);
						}
						
						return true;
					}
					
					return false;
				}});			
			
			list.setAdapter(a);
			a.notifyDataSetChanged();
	}	
}