package com.pino.smsparser;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_sample: {
				// Put sample data to db
				String[] date = {"13 May 1985, Mon", "14 May 1985, Mon", "15 May 1985, Mon", "16 May 1985, Mon", "17 May 1985, Mon", "18 May 1985, Mon", "19 May 1985, Mon", "20 May 1985, Mon", "21 May 1985, Mon", "22 May 1985, Mon", "23 May 1985, Mon", "24 May 1985, Mon", "25 May 1985, Mon", "26 May 1985, Mon", "27 May 1985, Mon", "28 May 1985, Mon", "29 May 1985, Mon"};
				String[] time = {"13:13:13", "14:14:14", "15:15:15", "16:16:16", "17:17:17", "18:18:18", "19:19:19", "20:20:20", "21:21:21", "22:22:22", "23:23:23", "00:00:00", "01:01:01", "02:02:02", "03:03:03", "04:04:04"};
				Integer[] type = {0,0,1,0,1,1,0,1,0,0,1,1,1,0,1,1};
				Double[] money = {10.5,50.1,71.0,60.3,21.8,31.3,40.0,81.4,54.2,31.1,19.4,19.0,17.6,38.4,15.4,37.8};
				
				for (int i=0; i<16; i++) {
					MainActivity.smsdb.addTransaction(date[i], time[i], type[i], money[i]);
				}
				
				updateList();
				break;
			}
			case R.id.menu_dialog: {
			// Show dialog here
			//showDialog(1); // Deprecated, use instead: http://android-developers.blogspot.in/2012/05/using-dialogfragments.html
				DialogFragment dlg = new TotalDlg();
				dlg.show(getFragmentManager(), "totaldlg");
				break;
			}
			case R.id.menu_drop : {
				smsdb.flushDatabase();	
				updateList();
				break;
			}
		}
		
	    return true;
	}
	
	public static void updateList() {
			Cursor cur = smsdb.getTransactions();
			
			Log.d("pino", Integer.toString(cur.getCount()));
			
			String[] from = {"date", "time", "type", "money"};
			int[] to = {R.id.date, R.id.time, R.id.coins, R.id.money};
			
			SimpleCursorAdapter a = new SimpleCursorAdapter(CNTXT, R.layout.sms_holder, cur, from, to, 0);	
			
			a.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
					if (arg0.getId() == R.id.coins) {
						ImageView v = (ImageView)arg0;
						if (arg1.getInt(arg2) == 0) {
							// Decrease
							//arg0.setBackgroundColor(0xffffcccc);
							v.setImageResource(R.drawable.coins_delete);
						}
						else if (arg1.getInt(arg2) == 1) {
							// Increase
							v.setImageResource(R.drawable.coins_add);							
						}
						else if (arg1.getInt(arg2) == 2) {
							// Total
							v.setImageResource(R.drawable.coins);					
						}
						else {
							// Undefined, don't touch at all
							//arg0.setBackgroundColor(0xff000000);
						}
						
						return true;
					}
					
					return false;
				}});			
			
			list.setAdapter(a);
			a.notifyDataSetChanged();
	}

	public void onFinishTotalDlg(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}	
	
}




































