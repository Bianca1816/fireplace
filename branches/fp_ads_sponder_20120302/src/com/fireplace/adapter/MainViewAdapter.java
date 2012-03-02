package com.fireplace.adapter;

import org.taptwo.android.widget.TitleProvider;

import com.fireplace.adsup.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainViewAdapter extends BaseAdapter implements TitleProvider {
	
	private LayoutInflater mInflater;
	
	private static final String[] names = {"Installed Apps","Featured Apps","Categories"};
	
	public MainViewAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return names.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//Implement Views from xml here to use full adapter
		
		int resId = 0;
        switch (position) {
        case 0:
            resId = R.layout.one;
            break;
        case 1:
            resId = R.layout.two;
            break;
        case 2:
            resId = R.layout.three;
            break;
        } 
		if(convertView == null) {
			convertView = mInflater.inflate(resId, null);
			
		}
		return convertView;
	}

	public String getTitle(int position) {
		return names[position];
	}

}
