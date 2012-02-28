package com.fireplace.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fireplace.software.ItemSkel;
import com.fireplace.adsup.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GetContentFromDBActivity extends ListActivity {
	final static String TAG = "GetContentFromDBActivty";
	ArrayList<String> stringArray = new ArrayList<String>();
	ArrayList<String> iconLocArray = new ArrayList<String>();
	ArrayList<Bitmap> iconArrayList = new ArrayList<Bitmap>();
	ArrayList<ItemSkel> list;
	IconicAdapter modeAdapter;
	boolean iconsReceived = false;
	boolean listReceived = false;
	ListView lv;
	ImageView icon;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofappswithicons);
		lv = (ListView) findViewById(android.R.id.list);
		modeAdapter = new IconicAdapter();
		new getListTask().execute();
		LoadData();
	}

	/**
	 * This Asynchronous task handles retrieving the list items from the server.
	 *
	 */
	private class getListTask extends
			AsyncTask<String, Integer, ArrayList<ItemSkel>> {

		@Override
		protected ArrayList<ItemSkel> doInBackground(String... params) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpGet httpGet = new HttpGet(
						"http://www.fireplace-market.com/getdata.php");
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				String result = "";

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				String line = null;
				while ((line = reader.readLine()) != null)
					result += line;

				Type type = new TypeToken<ArrayList<ItemSkel>>() {
				}.getType();
				Gson g = new Gson();
				list = g.fromJson(result, type);
				listReceived = true;				
				return list;
			} catch (JsonSyntaxException e) {
				Log.e(TAG, e.getMessage());
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
			} catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

	}

	/**
	 * This loads data for the list items...
	 */
	@SuppressWarnings("unchecked")
	void LoadData() {
		
		//This waits for 1/100 of a second periods until the list is received.
		while(listReceived == false){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		//Make sure list is not null before proceeding to use list to file array's
		if (list != null) {
			try {

				if (!stringArray.isEmpty())
					stringArray.clear();
				if (!iconLocArray.isEmpty())
					iconLocArray.clear();
				for (ItemSkel item : list) {
					stringArray.add(item.getLabel());
					iconLocArray.add(item.getIcon());
				}

				try {
					new IconDownloadTask().execute(iconLocArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				lv.setAdapter(modeAdapter);
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ItemSkel currentItem = list.get(position);
						Intent i = new Intent(getApplicationContext(),
								DownloadFileActivity.class);
						i.putExtra("title", currentItem.getLabel());
						if (iconsReceived) {
							i.putExtra("icon", iconArrayList.get(position));
						} else {
							i.putExtra("icon", ((BitmapDrawable) getResources()
									.getDrawable(R.drawable.icon)).getBitmap());
						}
						i.putExtra("link", currentItem.getPath());
						i.putExtra("desc", currentItem.getDescription());
						i.putExtra("ptype", currentItem.getPtype());
						i.putExtra("devl", currentItem.getDeveloper());
						startActivity(i);
					}
				});

			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());

			}
		} else {
			Toast.makeText(GetContentFromDBActivity.this,
					"Could not connect.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This is a custom adapter that make sit easier to work with icon's
	 * for the list.
	 */
	private class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(GetContentFromDBActivity.this, R.layout.approw_row,
					R.id.label, stringArray);
		}

		/* (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 * 
		 * This is where we populate each view in the lsit view.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			icon = (ImageView) row.findViewById(R.id.icon);
			if (iconArrayList.size() > 0) {
				icon.setImageBitmap(iconArrayList.get(position));
			} else {
				icon.setImageResource(R.drawable.icon);
			}
			return (row);
		}

	}

	/**
	 * This Asynchronous task is used for downloading the icon's in the background
	 * and when completed, it notifies the custom adapter that it's data has changed,
	 * which triggers the replacement of anything that has changed, more particularly the icons.
	 */
	private class IconDownloadTask extends
			AsyncTask<ArrayList<String>, Integer, ArrayList<Bitmap>> {
		@Override
		protected ArrayList<Bitmap> doInBackground(ArrayList<String>... params) {

			ArrayList<String> iconURLs = params[0];

			ArrayList<Bitmap> bitmapArrayList = new ArrayList<Bitmap>();
			for (String url_string : iconURLs) {

				try {
					URL url = new URL(url_string);
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					bitmapArrayList.add(BitmapFactory.decodeStream(bis));
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
					// This throws the default icon image into the array list
					// when it fails to download an image. Most likely because
					// the icon is not in the db.
					// This makes for an easy check when the activity first
					// runs.
					Bitmap bm;
					bm = ((BitmapDrawable) getResources().getDrawable(
							R.drawable.icon)).getBitmap();
					bitmapArrayList.add(bm);
				}

			}
			iconArrayList = bitmapArrayList;
			return bitmapArrayList;
		}

		@Override
		protected void onPostExecute(ArrayList<Bitmap> bitmapArrayList) {
			if (bitmapArrayList != null) {
				iconsReceived = true;
				//Notify custom adapter that the data has changed.
				modeAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getApplicationContext(),
						"Failed to Download Images", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
