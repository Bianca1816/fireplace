package com.fireplace.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fireplace.software.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewInfoActivity extends Activity implements android.view.View.OnClickListener{
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.conatininfo);
        setTitle("Application information");
        
        //-------- Buttons -------//
        Button btnDownload = (Button)findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(this);
        
        Bundle extras = getIntent().getExtras();
        
		if(extras !=null) {
		    //String value = extras.getString("title");
		    TextView txtTitle = (TextView) findViewById( R.id.lbAppTitle);
		    txtTitle.setText (extras.getString("title"));
		    TextView txtDesc = (TextView) findViewById( R.id.lbInfo);
		    txtDesc.setText (extras.getString("desc"));
		    TextView txtCategory = (TextView)findViewById(R.id.lbCategory);
		    txtCategory.setVisibility(View.GONE);
		    txtCategory.setText("Category: " + extras.getString("ptype"));
		    ImageView imgView = (ImageView) findViewById(R.id.imageView1);
		    Bitmap bm = (Bitmap) extras.get("icon");
		    imgView.setImageBitmap(bm);
		}
		
        }

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		//Button btnDownload = (Button)findViewById(R.id.btnDownload);
		//btnDownload.setText("Hello");
		
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) { 
		
		case R.id.btnDownload:
            	//Download Item from getPath
				Bundle extras = getIntent().getExtras(); 
				if(extras !=null) {
				    String sLink = extras.getString("link");
				    //String sTitle = extras.getString("title");
				    String sAppTitle = extras.getString("title") + ".apk";
				    
				   
				
	            	  try{
	            		  
	            		  //ItemSkel currentItem = list.get(position);
	            		  Toast.makeText(ViewInfoActivity.this, "Downloading " + sLink, Toast.LENGTH_LONG).show();
	              			
	                          //set the download URL, a url that points to a file on the internet
	                          //this is the file to be downloaded
	                          
	                      	
	                          URL url = new URL(sLink);

	                          //create the new connection
	                          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	                          //set up some things on the connection
	                          urlConnection.setRequestMethod("GET");
	                          urlConnection.setDoOutput(true);

	                          //and connect!
	                          urlConnection.connect();

	                          //set the path where we want to save the file
	                          //in this case, going to save it on the root directory of the
	                          //sd card.
	                          File SDCardRoot = Environment.getExternalStorageDirectory();
	                          //create a new file, specifying the path, and the filename
	                          //which we want to save the file as.
	                          File file = new File(SDCardRoot + "/Fireplace/" + sAppTitle + ".apk");

	                          //this will be used to write the downloaded data into the file we created
	                          FileOutputStream fileOutput = new FileOutputStream(file);

	                          //this will be used in reading the data from the internet
	                          InputStream inputStream = urlConnection.getInputStream();

	                          //this is the total size of the file
	                          int totalSize = urlConnection.getContentLength();
	                          //variable to store total downloaded bytes
	                          int downloadedSize = 0;
	                          //create a buffer...
	                          byte[] buffer = new byte[1024];
	                          int bufferLength = 0; //used to store a temporary size of the buffer

	                          //now, read through the input buffer and write the contents to the file
	                          while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                                  //add the data in the buffer to the file in the file output stream (the file on the sd card
	                                  fileOutput.write(buffer, 0, bufferLength);
	                                  //add up the size so we know how much is downloaded
	                                  downloadedSize += bufferLength;
	                                  //this is where you would do something to report the prgress, like this maybe
	                                  updateProgress(downloadedSize, totalSize);

	                          }
	                          //close the output stream when done
	                          fileOutput.close();
	                          
	                          File appFile = new File("/sdcard/Fireplace/" + File.separator + sAppTitle + ".apk");
	                          Intent installIntent = new Intent(Intent.ACTION_VIEW);
	                          installIntent.setDataAndType(Uri.fromFile(appFile),"application/vnd.android.package-archive");
	                          startActivity(installIntent);
	                          
	                  //catch some possible errors...
	                  } catch (MalformedURLException e) {
	                          e.printStackTrace();
	                  } catch (IOException e) {
	                          e.printStackTrace();
	                  }
	              }
		}
		}
		
	
	public void updateProgress(int currentSize, int totalSize)
	{ 
		//TextView mProgressText = (TextView)findViewById(R.id.lbDeveloper);
		//mProgressText.setText(Long.toString((currentSize/totalSize)*100)+"%"); 
		//Toast.makeText(ViewInfoActivity.this, ((currentSize/totalSize)*100)+"%", Toast.LENGTH_LONG).show();
		
	}
}
