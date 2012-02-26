package com.fireplace.software;


import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fireplace.software.R;

public class StorageActivity extends Activity implements OnClickListener
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage);
        
        //--------------- TEXTVIEW INFORMATION LOAD ---------------//
        TextView infoStorage = (TextView) findViewById(R.id.infoStorage);
        TextView infoOS = (TextView) findViewById(R.id.infoOS);
        TextView infoDevice = (TextView) findViewById(R.id.infoDevice);
        TextView infoModel = (TextView) findViewById(R.id.infoModel);
        TextView infoFireplace = (TextView) findViewById(R.id.infoFireplace);
        
        Button btnDonate = (Button) findViewById (R.id.btnDonate);
        btnDonate.setOnClickListener(this);
        
        Button btnClearTemp = (Button) findViewById (R.id.btnCleartmp);
        btnClearTemp.setOnClickListener(this);
        
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        Log.e("","Available MB : "+megAvailable);
        
        infoStorage.setText("" + megAvailable + " MB");
        // infoOS.setText(System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
        infoDevice.setText(android.os.Build.DEVICE);
        infoModel.setText(android.os.Build.MODEL);
        infoOS.setText(android.os.Build.VERSION.RELEASE);
        infoFireplace.setText("1.6");

        
        
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.btnDonate:
			Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=DC4WKW2AJFE4S&lc=US&item_name=Stian%20W%20Insteb%c3%b8%20%28Spxc%29&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted") );
			startActivity( browse );
			break;
	    
		case R.id.btnCleartmp:

			Toast.makeText(StorageActivity.this, "Temp files cleared", Toast.LENGTH_LONG).show();
			
			

			break;

		}
	}
}