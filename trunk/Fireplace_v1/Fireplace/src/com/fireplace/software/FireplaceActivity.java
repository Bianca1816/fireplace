package com.fireplace.software;



import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.fireplace.software.R;

public class FireplaceActivity extends Activity implements OnClickListener
{
	
    //private File currentDir;
    //private FileArrayAdapter adapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
       // currentDir = new File("/sdcard/");
        //fill(currentDir);
        
        
        
        TabHost th = (TabHost) findViewById (R.id.tabhost);
        th.setup();
        
        // Tab1
        TabSpec ts = th.newTabSpec("tag1"); //ts = TabSpec
        ts.setContent(R.id.tab1);
        ts.setIndicator("Home");
        th.addTab(ts);
        
        // Tab2
        ts = th.newTabSpec("tag2"); //ts = TabSpec
        ts.setContent(R.id.tab2);
        ts.setIndicator("Browse");
        th.addTab(ts);
        
        // Tab 3
        ts = th.newTabSpec("tag3"); //ts = TabSpec
        ts.setContent(R.id.tab3);
        ts.setIndicator("Manage");
        th.addTab(ts);
        
     // Tab 4
        ts = th.newTabSpec("tag4"); //ts = TabSpec
        ts.setContent(R.id.tab4);
        ts.setIndicator("Search");
        th.addTab(ts);
        
        Button btnRepo = (Button) findViewById (R.id.btnRepo);
        btnRepo.setOnClickListener(this);
        
        Button btnPack = (Button) findViewById (R.id.btnPack);
        btnPack.setOnClickListener(this);
        
        Button btnStorage = (Button) findViewById (R.id.btnStorage);
        btnStorage.setOnClickListener(this);
        
        Button btnViewAll = (Button) findViewById (R.id.btnViewAll);
        btnViewAll.setOnClickListener(this);
        
        Button btnUpdate = (Button) findViewById (R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        
        TextView txtDeviceInfo = (TextView) findViewById (R.id.txtDeviceInfo);
        txtDeviceInfo.setText("Android: " + android.os.Build.VERSION.RELEASE + "/ Device: " + android.os.Build.DEVICE);
        
     // create a File object for the parent directory
        File wallpaperDirectory = new File("/sdcard/Fireplace/com.sources.packages/");
        // have the object build the directory structure, if needed.
        wallpaperDirectory.mkdirs();
        // create a File object for the output file
        //File outputFile = new File(wallpaperDirectory, filename);
        // now attach the OutputStream to the file object, instead of a String representation
        //FileOutputStream fos = new FileOutputStream(outputFile);

        
        //com.update.packages.zip
        
        Process p;  
        try {  
           // Preform su to get root privledges  
           p = Runtime.getRuntime().exec("su");   
          
           // Attempt to write a file to a root-only  
           DataOutputStream os = new DataOutputStream(p.getOutputStream());  
           os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");  
          
           // Close the terminal  
           os.writeBytes("exit\n");  
           os.flush();  
           try {  
              p.waitFor();  
                   if (p.exitValue() != 255) {  
                      // TODO Code to run on success  
                	   //Toast.makeText(this, "Root Acces Granted", Toast.LENGTH_SHORT).show(); 
                   }  
                   else {  
                       // TODO Code to run on unsuccessful  
                	   Toast.makeText(this, "You need a rooted phone!", Toast.LENGTH_SHORT).show(); 
                   }  
           } catch (InterruptedException e) {  
              // TODO Code to run in interrupted exception  
        	   Toast.makeText(this, "You need a rooted phone!", Toast.LENGTH_SHORT).show(); 
           }  
        } catch (IOException e) {  
           // TODO Code to run in input/output exception  
            Toast.makeText(this, "You need a rooted phone!", Toast.LENGTH_SHORT).show();
        }  
    
        
        
        //Unzip packages files, and listing
        
    	//String zipFile = Environment.getExternalStorageDirectory() + "/Fireplace/com.update.packages.zip"; 
    	//String unzipLocation = Environment.getExternalStorageDirectory() + "/Fireplace/com.sources.packages/"; 
    	 
    	//Decompress d = new Decompress(zipFile, unzipLocation); 
    	//d.unzip(); 
    	//Toast.makeText(this, "Done: Packages", Toast.LENGTH_SHORT).show();
    
        
        WebView wv = (WebView) findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient());
        WebSettings webSettings = wv.getSettings();
        webSettings.setBuiltInZoomControls(false);
        wv.loadUrl("http://feedity.com/wordpress-com/UFtTUlBT.rss");   
    }
        
    
    public void updateProgress(int currentSize, int totalSize){
    	//Toast.makeText(this, "Packages: " + Long.toString((currentSize/totalSize)*100)+"% Complete", Toast.LENGTH_SHORT).show();
    	}

    public void onClick(View v) {
    	switch(v.getId()) {    	
    	
    	case R.id.btnRepo: //Repository button
    		//Show repo's
    		final Context contextRepo = this;
    		
    		
    		Intent intentRepo = new Intent(contextRepo, RepoActivity.class);
    		startActivityForResult(intentRepo, 0);    		
    		break;
    		
    	case R.id.btnPack: //Packages button
    		//Show packages installed
    		final Context contextPack = this;
    		
    		
    		Intent intentPack = new Intent(contextPack, ListInstalledApps.class);
    		startActivityForResult(intentPack, 0);    		
    		break;
    		
    	case R.id.btnStorage: //Storage button
    		//Show storage left on phone and SD card
    		final Context contextStorage = this;
    		
    		
    		Intent intentStorage = new Intent(contextStorage, StorageActivity.class);
    		startActivityForResult(intentStorage, 0);    		
    		break;    	
    		
    	case R.id.btnViewAll:
    		final Context contextStorage2 = this;
    		
    		
    		Intent intentStorage2 = new Intent(contextStorage2, ViewAllAppsActivity.class);
    		startActivityForResult(intentStorage2, 0);    		
    		break;   
    		
    	case R.id.btnUpdate:
    		
    		//Toast.makeText(FireplaceActivity.this, "Downloading:  " + "" + "", Toast.LENGTH_LONG).show();  		
try {
    			
    			String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
                
                int icon = R.drawable.icon;
                CharSequence tickerText = "Downloading update";
                long when = System.currentTimeMillis();
                Notification notification = new Notification(icon, tickerText, when);
                
                Context context = getApplicationContext();
                CharSequence contentTitle = "Downloading update";
                CharSequence contentText = "Fireplace_update.apk";
                //Intent notificationIntent = new Intent(this, App.class);
                //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                notification.setLatestEventInfo(context, contentTitle, contentText, null);
                
                final int HELLO_ID = 1;

                mNotificationManager.notify(HELLO_ID, notification);
    			
                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
            	//Toast.makeText(this, "Preparing: Packages", Toast.LENGTH_SHORT).show();
            	
                URL url = new URL("http://fireplacemarked.x90x.net/uploads/Fireplace_update.apk");

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
                File file = new File(SDCardRoot + "/Fireplace/Fireplace_update.apk");

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

        //catch some possible errors...
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    		
    		File appFile = new File("/sdcard/Fireplace/Fireplace_update.apk");
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(Uri.fromFile(appFile),"application/vnd.android.package-archive");
            startActivity(installIntent);
            
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            
            int icon = R.drawable.icon;
            CharSequence tickerText = "Update";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);
            
            Context context = getApplicationContext();
            CharSequence contentTitle = "Fireplace is updated";
            CharSequence contentText = "Install complete";
            //Intent notificationIntent = new Intent(this, App.class);
            //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText, null);
            
            final int HELLO_ID = 1;

            mNotificationManager.notify(HELLO_ID, notification);
    		break;
    	}
    }
}