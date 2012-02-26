package com.fireplace.software;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.fireplace.software.R;

public class PackagesActivity extends Activity implements OnClickListener
{
	
    //private File currentDir;
    //private FileArrayAdapter adapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.packages);
        Button btnBack = (Button) findViewById(R.id.button1);
        btnBack.setOnClickListener(this);
        
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
		//break;
		
	}
}