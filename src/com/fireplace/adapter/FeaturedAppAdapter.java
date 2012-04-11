package com.fireplace.adapter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireplace.adsup.R;
import com.fireplace.software.ItemSkel;

public class FeaturedAppAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ItemSkel> mFeatApps;
	private Drawable mStdImg;
	FeatAppViewHolder holder;

	public FeaturedAppAdapter(Context context){
		
		mInflater = LayoutInflater.from(context);
		mStdImg = context.getResources().getDrawable(R.drawable.ic_no_icon);
	}
	
	public int getCount() {
		return mFeatApps.size();
	}

	public Object getItem(int position) {
		return mFeatApps.get(position);
	}

	public long getItemId(int position) {
		return Long.parseLong(mFeatApps.get(position).getId());
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		
		if(convertView == null) {
	         convertView = mInflater.inflate(R.layout.featured_app, null);
	         
	         // creates a ViewHolder and stores a reference to the children view we want to bind data to
	         holder = new FeatAppViewHolder();
	         holder.mAppTitle = (TextView) convertView.findViewById(R.id.lbAppTitle);
	         holder.mIcon = (ImageView) convertView.findViewById(R.id.imageView1);
	         holder.mDesc = (TextView) convertView.findViewById(R.id.lbInfo);
	         holder.mDevl = (TextView) convertView.findViewById(R.id.lbDevlName);
	         convertView.setTag(holder);
	      } else { 
	         // reuse/overwrite the view passed assuming(!) that it is castable!
	         holder = (FeatAppViewHolder) convertView.getTag();
	      }
		
		ItemSkel itemSkel = mFeatApps.get(position);
		holder.setTitle(itemSkel.getLabel());
		holder.setDescription(itemSkel.getDescription());
		holder.setDeveloper(itemSkel.getDeveloper());
		if (itemSkel.getIcon() == null || itemSkel.getIcon().length() == 0) {
			holder.setIcon(mStdImg);
		} else {
			GetIconForHolderTask gifHolder = new GetIconForHolderTask(itemSkel);
			gifHolder.execute();
	    }
		
		return convertView;
	}
	
	private class GetIconForHolderTask extends AsyncTask<Void, Integer, Void>{

		public ItemSkel itemSkel;
		public GetIconForHolderTask(ItemSkel iitemSkel){
			itemSkel = iitemSkel;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				URL url = new URL(itemSkel.getIcon());
				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream is = connection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				holder.setIcon(new BitmapDrawable(BitmapFactory.decodeStream(bis)));
				bis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){
			
		}
		
	}
	
	public void setListItems(ArrayList<ItemSkel> listFeat){
		mFeatApps = listFeat;
	}
	
	public class FeatAppViewHolder {
		private TextView mAppTitle;
		private TextView mDesc;
		private TextView mDevl;
		private ImageView mIcon;
		
		public void setTitle(String title){
			mAppTitle.setText(title);
		}
		
		public void setDescription(String desc){
			mDesc.setText(desc);
		}
		
		public void setDeveloper(String devl){
			mDevl.setText(devl);
		}
		
		public void setIcon(Drawable img){
			if (img != null) {
	            mIcon.setImageDrawable(img);
	         }
		}

	}

}
