package com.fireplace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fireplace.service.DatabaseSyncService;

public class AlarmReceiver extends BroadcastReceiver {
	 
    private static final String TAG = "AlarmReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Recurring alarm; requesting synchronization service.");

        Intent synchronize = new Intent(context, DatabaseSyncService.class);
        context.startService(synchronize);
    }
 
}
