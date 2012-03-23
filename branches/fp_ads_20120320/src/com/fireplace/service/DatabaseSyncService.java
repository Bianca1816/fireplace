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
		
//		int counter = 0;
//		
//		while (!hasGoodNetwork() && counter != 3){
//			Log.i(TAG, "Network unavailable, sleeping for 5 minutes.");
//			try {
//				Thread.sleep(300000);
//			} catch (Exception e) {
//				Log.w(TAG, "Error in Thread Sleep", e);
//			}
//			counter++;
//			
//		}
		
		syncProcess();
		
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
				
				fireDB.commit();
				fireDB.close();
			}
		}
	}
	
}
