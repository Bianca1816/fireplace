package com.fireplace.activity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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

import com.fireplace.database.FireDB;
import com.fireplace.software.ItemSkel;
import com.fireplace.software.R;

public class GetContentLocalActivity extends ListActivity {
	private final static String TAG = "GetContentLocalActivity";
	
	private ArrayList<String> appNameArrayList = new ArrayList<String>();
	private ArrayList<String> iconLocationArrayList = new ArrayList<String>();
	private ArrayList<Bitmap> iconArrayList = new ArrayList<Bitmap>();
	
	private ArrayList<ItemSkel> itemSkelArrayList;
	
	private Integer ptype;
	private IconicAdapter iconAdapter;
	private boolean iconsReceived, listReceived = false;
	private ListView appListView;
	private ImageView iconImageView;
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofappswithicons);
		appListView = (ListView) findViewById(android.R.id.list);
		
		iconAdapter = new IconicAdapter();
		Bundle extras = getIntent().getExtras();

		itemSkelArrayList = new ArrayList<ItemSkel>();
				
		ptype = extras.getInt("position");
		
		new GetListTask().execute();
		LoadData();
	}

	/**
	 * This Asynchronous task handles retrieving the list items from the local database.
	 *
	 */
	private class GetListTask extends
			AsyncTask<String, Integer, ArrayList<ItemSkel>> {

		@Override
		protected ArrayList<ItemSkel> doInBackground(String... params) {
			try {

				itemSkelArrayList = getAppsFromFireDB();

				if (ptype != 0){
					for(Iterator<?> it = itemSkelArrayList.iterator(); it.hasNext();){
						if (!((ItemSkel)it.next()).getPtype().equalsIgnoreCase(ptype.toString())){
							it.remove();
						}
					}
				}
				listReceived = true;				
				return itemSkelArrayList;

			} catch (IllegalStateException e) {
				Log.e(TAG, "getListTask", e);
			}
			return null;
		}

	}
	
	private ArrayList<ItemSkel> getAppsFromFireDB(){
		FireDB db = new FireDB(getApplicationContext());
		ArrayList<ItemSkel> itemSkelArrayList = new ArrayList<ItemSkel>();
		
		db.open();
		Cursor c = db.getApps();
		
		if(c.moveToFirst()){
			for (int i = 0; i < c.getCount(); i++) {
				ItemSkel item = new ItemSkel();
				
				item.setId(c.getString(0));
				item.setLabel(c.getString(1));
				item.setPath(c.getString(2));
				item.setPtype(c.getString(3));
				item.setIcon(c.getString(4));
				item.setDescription(c.getString(5));
				item.setDeveloper(c.getString(6));
				item.setStatus(c.getString(7));
				itemSkelArrayList.add(item);
				
				c.moveToNext();
			}
		}
		
		c.close();
		db.close();
		
		return itemSkelArrayList;
	}

	/**
	 * This loads data for the list items...
	 */
	@SuppressWarnings("unchecked")
	private void LoadData() {
		
		//This waits for 1/100 of a second periods until the list is received.
		while(listReceived == false){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Log.e(TAG, "Sleep in LoadData", e);
			}
		}
		
		if (!itemSkelArrayList.isEmpty()) {
			try {

				if (!appNameArrayList.isEmpty())
					appNameArrayList.clear();
				if (!iconLocationArrayList.isEmpty())
					iconLocationArrayList.clear();
				for (int i = 0; i < itemSkelArrayList.size(); i++) {
					appNameArrayList.add(itemSkelArrayList.get(i).getLabel());
					iconLocationArrayList.add(itemSkelArrayList.get(i).getIcon());
				}

				try {
					new IconDownloadTask().execute(iconLocationArrayList);
				} catch (Exception e) {
					Log.e(TAG, "IconDL Task execution in LoadData", e);
				}
				
				appListView.setAdapter(iconAdapter);
				appListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ItemSkel currentItem = itemSkelArrayList.get(position);
						Intent i = new Intent(getApplicationContext(),
								DownloadFileActivity.class);
						i.putExtra("title", currentItem.getLabel());
						if (iconsReceived) {
							i.putExtra("icon", iconArrayList.get(position));
						} else {
							i.putExtra("icon", ((BitmapDrawable) getResources()
									.getDrawable(R.drawable.ic_no_icon)).getBitmap());
						}
						i.putExtra("link", currentItem.getPath());
						i.putExtra("desc", currentItem.getDescription());
						i.putExtra("ptype", currentItem.getPtype());
						i.putExtra("devl", currentItem.getDeveloper());
						startActivity(i);
					}
				});

			} catch (Exception ex) {
				Log.e(TAG, "LoadData", ex);

			}
		} else {
			Toast.makeText(GetContentLocalActivity.this,
					"No applications in this category...", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This is a custom adapter that makes it easier to work with icon's
	 * for the list.
	 */
	private class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(GetContentLocalActivity.this, R.layout.approw_row,
					R.id.label, appNameArrayList);
		}

		/* (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 * 
		 * This is where we populate each view in the list view.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			iconImageView = (ImageView) row.findViewById(R.id.icon);
			if (iconArrayList.size() > 0) {
				iconImageView.setImageBitmap(iconArrayList.get(position));
			} else {
				iconImageView.setImageResource(R.drawable.icon);
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
			for (int i = 0; i < iconURLs.size(); i++) {

				try {
					URL url = new URL(iconURLs.get(i));
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					bitmapArrayList.add(BitmapFactory.decodeStream(bis));
					bis.close();
				} catch (Exception e) {
					Log.w(TAG, "IconDownloadTask", e);

					Bitmap bm;
					bm = ((BitmapDrawable) getResources().getDrawable(
							R.drawable.ic_no_icon)).getBitmap();
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
				iconAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getApplicationContext(),
						"Failed to Download Images", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
