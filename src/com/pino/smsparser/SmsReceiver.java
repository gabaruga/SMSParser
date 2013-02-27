package com.pino.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {
	
	// originating sms sender
	private static final String ORG = "666";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get the SMS map from Intent
        Bundle extras = intent.getExtras();
        
        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( "pdus" );
             
            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                
                // check if sms was sent from the number we need
                if (sms.getOriginatingAddress().equals(ORG)) {
                	Log.d("pino", "Got a message");
                	
	                String body = sms.getMessageBody().toString();
	                
	                MainActivity.smsdb.addTransaction(	DateFormat.format("dd MMM yyyy, E",sms.getTimestampMillis()).toString(),
	                									DateFormat.format("hh:mm:ss",sms.getTimestampMillis()).toString(),
	                									Integer.parseInt(body.substring(0, 1)),
	                									Double.parseDouble(body.substring(1))	);
                }
            }             
            
            MainActivity.updateList();
        }
	}
}