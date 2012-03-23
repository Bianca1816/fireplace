package com.fireplace.software;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DataFetch {
	
	final static String TAG = "DatabaseSyncService";
	
	
	public ArrayList<ItemSkel> getFromFirePlace(){
		return getFrom("http://www.fireplace-market.com/getdata.php");
	}
	
	public ArrayList<ItemSkel> getFromOtherRepo(String url){
		return getFrom(url);
	}

	private ArrayList<ItemSkel> getFrom(String url){
		ArrayList<ItemSkel> itemSkelArrayList = null;

		try {

			HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url),
					new BasicHttpContext());
			String result = "";

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line = null;
			while ((line = reader.readLine()) != null)
				result += line;

			itemSkelArrayList = new Gson().fromJson(result, new TypeToken<ArrayList<ItemSkel>>() {}.getType());
			
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "getFromFirePlace", e);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "getFromFirePlace", e);
		} catch (IllegalStateException e) {
			Log.e(TAG, "getFromFirePlace", e);
		} catch (IOException e) {
			Log.e(TAG, "getFromFirePlace", e);
		}
		
		return itemSkelArrayList;
	}
	
}
