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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DataFetch{
	
	final static String TAG = "DatabaseSyncService";
	private String url = "http://www.fireplace-market.com/getdata.php";

	public ArrayList<ItemSkel> getFromFirePlace(){
		return getFrom(url);
	}
	
	public ArrayList<ItemSkel> getFromOtherRepo(String url){
		return getFrom(url);
	}

	public ArrayList<ItemSkel> getFrom(String url){
		ArrayList<ItemSkel> itemSkelArrayList = new ArrayList<ItemSkel>();
		
		try {

			HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url),
					new BasicHttpContext());
			String result = "";

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line = null;
			while ((line = reader.readLine()) != null)
				result += line;

			try {
				JSONArray jsonArray = new JSONArray(result);
				Log.i(TAG, "Number of entries " + jsonArray.length());
				
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ItemSkel item = new ItemSkel();
					item.setId(jsonObject.getString("id"));
					item.setLabel(jsonObject.getString("label"));
					item.setPath(jsonObject.getString("path"));
					item.setPtype(jsonObject.getString("ptype"));
					item.setIcon(jsonObject.getString("icon"));
					item.setDescription(jsonObject.getString("description"));
					item.setDeveloper(jsonObject.getString("devel"));
					item.setStatus(jsonObject.getString("status"));
					itemSkelArrayList.add(item);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
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
