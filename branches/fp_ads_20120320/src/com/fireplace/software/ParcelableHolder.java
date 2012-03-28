package com.fireplace.software;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;
 
public class ParcelableHolder implements Parcelable {
    private HashMap map = null;
 
    public ParcelableHolder() {
        map = new HashMap();
    }
 
    public ParcelableHolder(Parcel in) {
        map = new HashMap();
        readFromParcel(in);
    }
 
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableHolder createFromParcel(Parcel in) {
            return new ParcelableHolder(in);
        }
 
        public ParcelableHolder[] newArray(int size) {
            return new ParcelableHolder[size];
        }
    };
  
    public void readFromParcel(Parcel in) {
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            map.put(in.readString(), in);
        }
    }
 
    public Object get(String key) {
        return map.get(key);
    }
 
    public void put(String key, Object value) {
        map.put(key, value);
    }

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(map.size());
        for (Object s: map.keySet()) {
            dest.writeString(s.toString());
            dest.writeValue(map.get(s));
        }
	}
}
