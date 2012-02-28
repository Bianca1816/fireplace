package com.fireplace.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.fireplace.adsup.R;
import com.fireplace.software.ChangeLog;
import com.fireplace.software.ItemSkel;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class FireplaceActivity extends ListActivity implements OnClickListener {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();

	// DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	ArrayAdapter<String> adapter;

	// RECORDING HOW MUCH TIMES BUTTON WAS CLICKED
	int clickCounter = 1;
	private EditText etSearchText;
	AdView adView;
	final static String TAG = "FireplaceActivity";
	ArrayList<ItemSkel> list;
	boolean listReceived = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		ChangeLog cl = new ChangeLog(this);
        if (cl.firstRun())
            cl.getLogDialog().show();
		
        adView = (AdView) findViewById(R.id.adView);
        
        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
		listItems.add("Network Tools");
		listItems.add("Root utilities");
		listItems.add("System Tools");
		listItems.add("Security");
		listItems.add("Tweaks");
		listItems.add("Themes");
		
		File folder = new File("/sdcard/Fireplace/");

		if (folder.exists()) {
			String deleteCmd = "rm -r " + "/sdcard/Fireplace/";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}

		TabHost th = (TabHost) findViewById(R.id.tabhost);
		th.setup();

		// Tab 1
		TabSpec ts = th.newTabSpec("tag1"); // ts = TabSpec
		ts.setContent(R.id.tab1);
		ts.setIndicator("Home");
		th.addTab(ts);

		// Tab 2
		ts = th.newTabSpec("tag2"); // ts = TabSpec
		ts.setContent(R.id.tab2);
		ts.setIndicator("Manage");
		th.addTab(ts);

		// Tab 3
		ts = th.newTabSpec("tag3"); // ts = TabSpec
		ts.setContent(R.id.tab3);
		ts.setIndicator("Browse");
		th.addTab(ts);
		
		// Tab 4
		ts = th.newTabSpec("tag4"); // ts = TabSpec
		ts.setContent(R.id.tab4);
		ts.setIndicator("Search");
		th.addTab(ts);
		
		TextView txtLoading = (TextView) findViewById(R.id.txtLoading);
		txtLoading.setText(getResources().getString(R.string.strSetUpComp)); // Initial loading

		Button btnRepo = (Button) findViewById(R.id.btnRepo);
		btnRepo.setOnClickListener(this);

		Button btnPack = (Button) findViewById(R.id.btnPack);
		btnPack.setOnClickListener(this);

		Button btnStorage = (Button) findViewById(R.id.btnStorage);
		btnStorage.setOnClickListener(this);

		Button btnViewAll = (Button) findViewById(R.id.btnViewAll);
		btnViewAll.setOnClickListener(this);

		Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnFacebook.setOnClickListener(this);

		Button btnTwitter = (Button) findViewById(R.id.btnTwitter);
		btnTwitter.setOnClickListener(this);

		TextView txtDeviceInfo = (TextView) findViewById(R.id.txtDeviceInfo);
		txtDeviceInfo.setText("Android: " + android.os.Build.VERSION.RELEASE
				+ "/ Device: " + android.os.Build.DEVICE);
		
		etSearchText = (EditText) findViewById(R.id.etSearchField);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);

		txtLoading.setText("Runnning network check");

		startupNetworkCheck();

		// create a dir
		txtLoading.setText("Loading folders");

		File fireplaceDir = new File("/sdcard/Fireplace/");
		fireplaceDir.mkdirs();

		txtLoading.setText("All done!");

		txtLoading.setVisibility(View.GONE);
		ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);
		pBar.setVisibility(View.GONE);
	}

	public void updateProgress(int currentSize, int totalSize) {
		// Toast.makeText(this, "Packages: " +
		// Long.toString((currentSize/totalSize)*100)+"% Complete",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.textDonate:
			Intent browse = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://www.paypal.com/us/cgi-bin/webscr?cmd=_flow&SESSION=Ix6KJPWgQAW6v-JBj3RnVjrNdIZAvQgsh3Yi01blXbL5tDo4PKyPeMVYDFy&dispatch=5885d80a13c0db1f8e263663d3faee8d4026841ac68a446f69dad17fb2afeca3"));
			startActivity(browse);
			break;

		case R.id.textAbout:
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setTitle("About Fireplace Market");
			alertbox.setMessage("Fireplace Market is a 3rd party app store which contain apps and tweaks which didn't get into Android Market"
					+ "\n\nThis software comes without any kind of warranty!"
					+ "\n\nProject started by Spxc"
					+ "\n\nCopyright 2012"
					+ "\nRooted Dev Team"
					+ "\nStian Insteb� & Sachira Chinthana Jayasanka");
			alertbox.setNeutralButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// Edit string firstTimeRun in strings.xml
						}
					});

			// show it
			alertbox.show();

			break;
		case R.id.textCheckUpdate:
			try {
				// set the download URL, a url that points to a file on the
				// internet
				// this is the file to be downloaded
				URL url = new URL("http://www.fireplace-market.com/fireplaceUpdate.v"
						+ getResources().getString(R.string.updateTo) + ".apk");

				// create the new connection
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				// set up some things on the connection
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);

				// and connect!
				urlConnection.connect();

				// set the path where we want to save the file
				// in this case, going to save it on the root directory of the
				// sd card.
				File SDCardRoot = Environment.getExternalStorageDirectory();
				// create a new file, specifying the path, and the filename
				// which we want to save the file as.
				File file = new File(SDCardRoot + "Fireplace/", "update.apk");

				// this will be used to write the downloaded data into the file
				// we created
				FileOutputStream fileOutput = new FileOutputStream(file);

				// this will be used in reading the data from the internet
				InputStream inputStream = urlConnection.getInputStream();

				// this is the total size of the file
				int totalSize = urlConnection.getContentLength();
				// variable to store total downloaded bytes
				int downloadedSize = 0;

				// create a buffer...
				byte[] buffer = new byte[1024];
				int bufferLength = 0; // used to store a temporary size of the
										// buffer

				// now, read through the input buffer and write the contents to
				// the file
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					// add the data in the buffer to the file in the file output
					// stream (the file on the sd card
					fileOutput.write(buffer, 0, bufferLength);
					// add up the size so we know how much is downloaded
					downloadedSize += bufferLength;
					// this is where you would do something to report the
					// prgress, like this maybe
					updateProgress(downloadedSize, totalSize);

				}
				// close the output stream when done
				fileOutput.close();

				Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(
						Uri.parse(SDCardRoot + "Fireplace/update.apk"))
						.setType("application/vnd.android.package-archive");
				startActivity(promptInstall);

				// catch some possible errors...
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// see
			// http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification

			Toast.makeText(this, "No new updates available!", Toast.LENGTH_LONG)
					.show();
			break;
		}

		return true;

	}

	public void onClick(View v) {
		if (isOnline()) {
			switch (v.getId()) {

			case R.id.btnTwitter: // Twitter Button
				Intent browsetwitter = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://twitter.com/#!/FireplaceMarket"));
				startActivity(browsetwitter);
				break;

			case R.id.btnFacebook: // Facebook Button
				Intent browseFacebook = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://www.facebook.com/pages/Fireplace-Market/379268035417930"));
				startActivity(browseFacebook);

				break;

			case R.id.btnRepo: // Repository button
				// Show repo's
				final Context contextRepo = this;

				Intent intentRepo = new Intent(contextRepo, RepoActivity.class);
				startActivityForResult(intentRepo, 0);
				break;

			case R.id.btnPack: // Packages button
				// Show packages installed
				final Context contextPack = this;

				Intent intentPack = new Intent(contextPack,
						ListInstalledAppsActivity.class);
				startActivityForResult(intentPack, 0);
				break;

			case R.id.btnStorage: // Storage button
				// Show storage left on phone and SD card
				final Context contextStorage = this;

				Intent intentStorage = new Intent(contextStorage,
						StorageActivity.class);
				startActivityForResult(intentStorage, 0);
				break;

			case R.id.btnViewAll:
				// Show all apps
				
					final Context contextAllApps = this;
					Toast.makeText(FireplaceActivity.this, "Loading apps...",
							Toast.LENGTH_LONG).show();
					Intent intentStorage2 = new Intent(contextAllApps,
							GetContentFromDBActivity.class);
					startActivityForResult(intentStorage2, 0);
				
				break;

			case R.id.btnSearch:
				
				
				
				break;
			}
		}
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

	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		Toast.makeText(FireplaceActivity.this, "You need network connection!",
				Toast.LENGTH_LONG).show();
		return false;
	}

	public boolean startupNetworkCheck() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			// Toast.makeText(FireplaceActivity.this, "Network enabled!",
			// Toast.LENGTH_LONG).show();
			return true;
		}
		Toast.makeText(FireplaceActivity.this,
				"No network connection detected!", Toast.LENGTH_LONG).show();
		Button btnViewAll = (Button) findViewById(R.id.btnViewAll);
		btnViewAll.setEnabled(false);
		return false;
	}

	public boolean updateChecketwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (isOnline()) {
			try {

				// set the download URL, a url that points to a file on the
				// internet
				// this is the file to be downloaded
				// Toast.makeText(this, "Preparing: Packages",
				// Toast.LENGTH_SHORT).show();
				String updateString = getString(R.string.updateTo);
				URL url = new URL(
						"http://www.u2worlds.com/fp/updates/Fireplace_update"
								+ updateString + ".apk");

				// create the new connection
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				// set up some things on the connection
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);

				// and connect!
				urlConnection.connect();

				// set the path where we want to save the file
				// in this case, going to save it on the root directory of the
				// sd card.

				File SDCardRoot = Environment.getExternalStorageDirectory();
				// create a new file, specifying the path, and the filename
				// which we want to save the file as.
				File file = new File(SDCardRoot + "/Fireplace/Fireplace_update"
						+ updateString + ".apk");

				// this will be used to write the downloaded data into the file
				// we created
				FileOutputStream fileOutput = new FileOutputStream(file);

				// this will be used in reading the data from the internet
				InputStream inputStream = urlConnection.getInputStream();

				// this is the total size of the file
				int totalSize = urlConnection.getContentLength();
				// variable to store total downloaded bytes
				int downloadedSize = 0;

				// create a buffer...
				byte[] buffer = new byte[1024];
				int bufferLength = 0; // used to store a temporary size of the
										// buffer

				// now, read through the input buffer and write the contents to
				// the file
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					// add the data in the buffer to the file in the file output
					// stream (the file on the sd card
					fileOutput.write(buffer, 0, bufferLength);
					// add up the size so we know how much is downloaded
					downloadedSize += bufferLength;
					// this is where you would do something to report the
					// prgress, like this maybe
					updateProgress(downloadedSize, totalSize);

				}
				// close the output stream when done
				fileOutput.close();

				// catch some possible errors...
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String updateString = getString(R.string.updateTo);

			File appFile = new File("/sdcard/Fireplace/Fireplace_update"
					+ updateString + ".apk");
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(Uri.fromFile(appFile),
					"application/vnd.android.package-archive");
			startActivity(installIntent);

			return true;
		}
		Toast.makeText(FireplaceActivity.this, "No update available",
				Toast.LENGTH_LONG).show();
		return false;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		etSearchText.setText("");
	}
	
	@Override
	  public void onDestroy() {
	    adView.destroy();
	    super.onDestroy();
	  }

}