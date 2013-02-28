package com.pino.smsparser;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	public static ListView list;
	public static SMSDB smsdb;
	public static Context CNTXT;	
	
	ViewPager vp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CNTXT = this;
		smsdb = new SMSDB(CNTXT);	 
		
		SwipeAdapter swA = new SwipeAdapter();
		vp = (ViewPager)findViewById(R.id.myfivepanelpager);
		vp.setAdapter(swA);
		vp.setCurrentItem(0);
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
				// Show dialog
				DialogFragment dlg = new TotalDlg();
				dlg.show(getSupportFragmentManager(), "totaldlg");
				break;
			}
			case R.id.menu_drop : {
				// Flush database
				smsdb.flushDatabase();	
				updateList();
				break;
			}
		}
		
	    return true;
	}
	
	public static void updateList() {
			Cursor cur = smsdb.getTransactions();
			
			String[] from = {"date", "time", "type", "money"};
			int[] to = {R.id.date, R.id.time, R.id.coins, R.id.money};
			
			SimpleCursorAdapter a = new SimpleCursorAdapter(CNTXT, R.layout.sms_holder, cur, from, to, 0);	
			
			a.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
					if (arg0.getId() == R.id.coins) {
						ImageView v = (ImageView)arg0;
						
						switch (arg1.getInt(arg2)) {
							case SMSDB.TR_DECR: {
								// Decrease							
								v.setImageResource(R.drawable.coins_delete);
								break;
							}
							case SMSDB.TR_INCR: {
								// Increase
								v.setImageResource(R.drawable.coins_add);
								break;
							}						
							case SMSDB.TR_TOTL: {
								// Total
								v.setImageResource(R.drawable.coins);
								break;
							}
							case SMSDB.TR_BASE: {
								// Base
								v.setImageResource(R.drawable.coins);
								break;
							}
						}
						return true;
					}
					
					return false;
				}});			
			
			list.setAdapter(a);
			a.notifyDataSetChanged();
	}

	public void onFinishTotalDlg(String string) {
		smsdb.setBaseline(Double.parseDouble(string));
		updateList();
	}	
	
	// ----------------------- Swipe section ------------------------------------
	public static class SwipeAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// return a number of, ermmm, screens
			return 2;
		}

		public Object instantiateItem(View collection, int position) {
            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int resId = 0;
            View view = null;
            Activity a = (Activity)CNTXT;
            
            switch (position) {
            case 0:            	           	
                resId = R.layout.activity_list;
                view = inflater.inflate(resId, null);
                ((ViewPager) collection).addView(view, 0);
                list = (ListView) view.findViewById(R.id.list);                
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                updateList();
                break;
            case 1:            	
                resId = R.layout.activity_plot;
                view = inflater.inflate(resId, null);
                ((ViewPager) collection).addView(view, 0);
                
                // this shit is working not as expected
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                
                // https://code.google.com/p/afreechart/
                
                break;
            }
            
            return view;
        }
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}
		
		@Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }
	}
}
