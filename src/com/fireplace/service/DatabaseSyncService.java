package com.fireplace.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.fireplace.database.Constants;
import com.fireplace.database.FireDB;
import com.fireplace.software.DataFetch;
import com.fireplace.software.ItemSkel;

public class DatabaseSyncService extends Service {

	private FireDB fireDB;
	private ArrayList<ItemSkel> itemSkelArrayList;
	final static String TAG = "DatabaseSyncService";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public boolean hasGoodNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	public void syncProcess(){
		if (hasGoodNetwork()) {
			
			Log.i(TAG, "Starting Sync");
			//fetch records from fireplace market server.
			itemSkelArrayList = new DataFetch().getFromFirePlace();
			
			if (itemSkelArrayList != null) {
				fireDB = new FireDB(this);
				fireDB.open();
				
				//drop table to recreate
				fireDB.dropTable(Constants.APPS_TABLE_NAME);
				
				fireDB.createProductTable();

				for (int i = 0; i < itemSkelArrayList.size(); i++) {
					ItemSkel s = itemSkelArrayList.get(i);
					fireDB.insertApp(s.getLabel(), s.getPath(),
							Integer.parseInt(s.getPtype()), s.getIcon(),
							s.getDescription(), s.getDeveloper(),
							Integer.parseInt(s.getStatus()));
				}
				
//				fireDB.commit();
				fireDB.close();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new Thread() {
		    @Override public void run() {
		    	syncProcess();
		    }
		  }.start();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
}
