package com.fireplace.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fireplace.software.R;

public class DownloadFileActivity extends Activity implements
		android.view.View.OnClickListener {

	public static final String LOG_TAG = "DownloadFileActivity";
	private DownloadFileAsync myAsyncDownloadTask = null;
	
	//Length to be set from Async task after any configuration change, if task is running.
	private int carryOverFileLength = 0;
	
	// initialize our progress dialog/bar
	private ProgressDialog mProgressDialog = null;
	private static final int DIALOG_DOWNLOAD_PROGRESS = 1;

	// initialize root directory
	File rootDir = Environment.getExternalStorageDirectory();

	public String fileURL;
	public String title;
	boolean goodConnection;

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * We check for previous configuration instacnes, and use them if available.
	 * Afterwards we proceed to grab display elements and particulars out of
	 * extras bundle.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Getting the extras from the previous activity
		Bundle extras = getIntent().getExtras();		
		
    	if (getLastNonConfigurationInstance() != null)
    	{    		
    		//If Async task is running, grab hook here, after configuration change
    		myAsyncDownloadTask = (DownloadFileAsync) getLastNonConfigurationInstance();
    		myAsyncDownloadTask.handlerOfCaller = myHandlerOfThis;
    		requestWindowFeature(Window.FEATURE_NO_TITLE);
    		
    		switch(myAsyncDownloadTask.getStatus())
    		{
    		case RUNNING:
    			//set file length here.
    			carryOverFileLength = myAsyncDownloadTask.lengthOfFile;
    			showDialog(DIALOG_DOWNLOAD_PROGRESS);
    			
    			break;
    		case PENDING:
    			if (mProgressDialog != null && mProgressDialog.isShowing()) {
    				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
    				myAsyncDownloadTask = null;
    			}
    			break;
    		case FINISHED:
    			if (mProgressDialog != null && mProgressDialog.isShowing()) {
    				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
    				myAsyncDownloadTask = null;
    			}
    			break;
			default:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
    				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
    				myAsyncDownloadTask = null;
    			}
				break;
    		}
    	}
	    
		if (extras != null) {
			setContentView(R.layout.conatininfo);
			
			Button btnShare = (Button) findViewById(R.id.btnShare);
			btnShare.setOnClickListener(this);
			Button btnDownload = (Button) findViewById(R.id.btnDownload);
			btnDownload.setOnClickListener(this);
			TextView txtTitle = (TextView) findViewById(R.id.lbAppTitle);
			title = extras.getString("title");
			txtTitle.setText(title);
			setTitle("About " + title);
			TextView txtDesc = (TextView) findViewById(R.id.lbInfo);
			txtDesc.setText(extras.getString("desc"));
			TextView txtDevlName = (TextView) findViewById(R.id.lbDevlName);
			txtDevlName.setText(extras.getString("devl"));
			TextView txtCategory = (TextView) findViewById(R.id.lbCategory);
			txtCategory.setVisibility(View.GONE);
			txtCategory.setText("Category: " + extras.getString("ptype"));
			ImageView imgView = (ImageView) findViewById(R.id.imageView1);
			Bitmap bm = (Bitmap) extras.get("icon");
			imgView.setImageBitmap(bm);

			// making sure the download directory exists
			checkAndCreateDirectory("/Fireplace");
			fileURL = extras.getString("link");

		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 * 
	 * We set up dialogs in this call, currently, there is only the progress dialog
	 * that appears for showing progress of download.
	 */
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id) {
		case DIALOG_DOWNLOAD_PROGRESS://1
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setMessage("Downloading application...");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setCancelable(false);
			} else {
				mProgressDialog.setMax(carryOverFileLength);
			}
			return mProgressDialog;
		default:
			return null;
		}
		
	}
	
	/* (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * This simply listens for a click on a specific View, currently only the download button.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDownload:
			// executing the asynctask
    		//To wrap the task below with if/else, we first need md5 values from db
    		//so we can check the files that are local to approve the attempted install.
    		//I need to look into how to resume file download, if network fails.
			myAsyncDownloadTask = new DownloadFileAsync(myHandlerOfThis);
			myAsyncDownloadTask.execute(fileURL);
			break;
		case R.id.btnShare:
			shareIt();
			break;
		}
	}

	private void shareIt() {
		//sharing implementation here
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = "I just installed " + title + " using Fireplace Market.";
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
		}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	/**
	 * This method checks to see if a directory exists on the root external storage
	 * directory and creates it if it does not exist.
	 * @param dirName is the directory that may or may not exist on the external storage directory.
	 */
	public void checkAndCreateDirectory(String dirName) {
		File new_dir = new File(rootDir + dirName);
		if (!new_dir.exists()) {
			new_dir.mkdirs();
		}
	}

	/**
	 * This is our Asynchronous task sub class. It allows for the downloading of the application
	 * apk's from the server, and report's back progress. It takes any number of Strings as it's
	 * execute argument (for doInBackground). The Integer is how it reports progress. The final
	 * String is what is returned from doInBackground and taken as an argument into postExecute.
	 */
	class DownloadFileAsync extends AsyncTask<String, Integer, String> {
		
		public final static int DOWNLOAD_COMPLETE = 0;
		public final static int DOWNLOAD_ERROR = 1;
		public final static int DOWNLOAD_PROGRESS = 2;
		
		public int lengthOfFile = 0;
		
		public Handler handlerOfCaller;
		
		/**
		 * Construstor
		 * 
		 * Used to pass in our handler, to allow for communication concerning the progress
		 * of the Async task.
		 * @param thisHandler is the Handler needed for progress communication to the UI process.
		 */
		public DownloadFileAsync (Handler thisHandler){
			handlerOfCaller = thisHandler;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {

			String fileUrl = params[0];

			try {

				// connecting to url
				URL url = new URL(fileUrl);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.connect();

				// lengthOfFile is used for calculating download progress
				lengthOfFile = c.getContentLength();
				mProgressDialog.setMax(lengthOfFile);

				// this is where the file will be seen after the download
				FileOutputStream f = new FileOutputStream(new File(rootDir
						+ "/Fireplace/", title + ".apk"));
				// file input is from the url
				InputStream in = null;
				
				//check server for good response to file location
				goodConnection = (c.getResponseCode() == HttpURLConnection.HTTP_OK) ? true : false;
				
				if (goodConnection) {

					in = new BufferedInputStream(c.getInputStream());
					byte[] buffer = new byte[1024];
					int len1 = 0;
					int total = 0;
					
					while ((len1 = in.read(buffer)) != -1) {
						total += len1;
						
						//Send message to report progress to UI process...
						Message thisMessage = handlerOfCaller.obtainMessage();
						thisMessage.arg1 = total;
						thisMessage.arg2 = lengthOfFile;
						thisMessage.what = DOWNLOAD_PROGRESS;
						handlerOfCaller.sendMessage(thisMessage);
						
						//write file to device
						f.write(buffer, 0, len1);
					}
					f.close();
				} else {
					Toast.makeText(DownloadFileActivity.this, "File not found on server...",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message thisMessage = handlerOfCaller.obtainMessage();
				thisMessage.what = DOWNLOAD_ERROR;
				handlerOfCaller.sendMessage(thisMessage);
			}

			return "";
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String unused) {
			// Send message to dismiss the dialog after the file is finished downloading.
			Message thisMessage = handlerOfCaller.obtainMessage();
			thisMessage.what = DOWNLOAD_COMPLETE;
			handlerOfCaller.sendMessage(thisMessage);

			// Grab file and launch install intent
			File appFile = new File("/sdcard/Fireplace/" + File.separator
					+ title + ".apk");
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(Uri.fromFile(appFile),
					"application/vnd.android.package-archive");
			startActivity(installIntent);

		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * This is our handler implementation that handles communication on the UI
	 * process from the Async task.
	 */
	final Handler myHandlerOfThis = new Handler()
	{
		public void handleMessage(Message message){
			
			switch (message.what)
			{
			case DownloadFileAsync.DOWNLOAD_PROGRESS:
				mProgressDialog.setProgress(message.arg1);
				break;
			case DownloadFileAsync.DOWNLOAD_ERROR:
				dismissProgressDialogIfAround();
				Toast.makeText(DownloadFileActivity.this, "File download error, please try again...",
						Toast.LENGTH_LONG).show();
				break;
			case DownloadFileAsync.DOWNLOAD_COMPLETE:
				dismissProgressDialogIfAround();
				break;
			default:
				dismissProgressDialogIfAround();
				break;

			}
		}
	};
	
	/**
	 * Convenience method to dismiss dialog if it exists and is showing.
	 */
	public void dismissProgressDialogIfAround(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
	}
	
	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 * 
	 * Drop reference to handler to re-attach in new activity when created
	 * from configuration change.
	 */
	@Override
	public Object onRetainNonConfigurationInstance()
	{
		dismissProgressDialogIfAround();
				
		if (myAsyncDownloadTask != null)
		{
			myAsyncDownloadTask.handlerOfCaller = null;
			return myAsyncDownloadTask;
		}
	
		return super.onRetainNonConfigurationInstance();		
	}
	
}
