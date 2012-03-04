package com.fireplace.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.taptwo.android.widget.TitleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.fireplace.adapter.AppListAdapter;
import com.fireplace.adapter.MainViewAdapter;
import com.fireplace.software.App;
import com.fireplace.software.ChangeLog;
import com.fireplace.software.R;

public class FireplaceActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	private List<String> listItems = new ArrayList<String>();

	// DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	private ArrayAdapter<String> adapter;
	ImageView gPlusView;
	ImageView twitView;
	ImageView fbView;
	
	private ListView categoryView;

	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Z3LN/g1lqWcvOt99MrKu4hfNTuoVQJIQV14KSG8QmrPWiuxWEY+0pZ1aXk2MkuSVyF0Zr0fMBQl+IHPzquSHpqP0eK08OjCD/I1L5u8hzI56D0eI6I05T7NaiLvD/tJ1LqVod0l7FFkWx0p9dnzaoixK2w7GUqWcEbr40LWeHMB5DpMxW3UqecMavgn1mct9DgVMOpOMusTGGVJ9g67tu/NcWosKYojYwLlD8v6Dy4zOn0LjWz3i/up1TZNSz77VxbiNxBpV7D012fiIKvQLQbeMzbkiMhJlavwy7ara/lRkN5DlU6GnnB+eBATXoltsCNeLQeop6zbdi1SQ4zrhQIDAQAB";
	private static final byte[] SALT = new byte[] { -26, 65, 30, -118, -113,
			-57, 74, -64, 53, 69, -95, -25, 71, -117, -36, -113, -11, 37, -64,
			49 };

	private LicenseCheckerCallback mLicenseCheckerCallback;
	private LicenseChecker mChecker;
	private Handler mHandler;
	private ViewFlow viewFlow;

	private MainViewAdapter mvAdapter;

	private static final boolean INCLUDE_SYSTEM_APPS = false;

	private ListView mAppsList;
	private AppListAdapter mAdapter;
	private List<App> mApps;
	private boolean iconsLoaded = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newviewflow);

		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mvAdapter = new MainViewAdapter(this);
	
		if(savedInstanceState == null){
			viewFlow.setAdapter(mvAdapter, 1);
		} else {
			viewFlow.setAdapter(mvAdapter, savedInstanceState.getInt("CurrentView"));
		}
		
		TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
		indicator.setTitleProvider(mvAdapter);
		viewFlow.setFlowIndicator(indicator);
		
		gPlusView = (ImageView) findViewById(R.id.googleCon);
		twitView = (ImageView) findViewById(R.id.twiiterCon);
		fbView = (ImageView) findViewById(R.id.fbCon);
		
		gPlusView.setImageResource(R.drawable.googleplus);
		twitView.setImageResource(R.drawable.twitter);
		fbView.setImageResource(R.drawable.fb);
		
		gPlusView.setOnClickListener(this);
		twitView.setOnClickListener(this);
		fbView.setOnClickListener(this);
		
		mHandler = new Handler();

		// Construct the LicenseCheckerCallback. The library calls this when done.
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();

		// SetUp device ID
		String deviceId = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID + Secure.LOGGING_ID);

		// Construct the LicenseChecker with a Policy.
		mChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
				new AESObfuscator(SALT, getPackageName(), deviceId)),
				BASE64_PUBLIC_KEY // Your public licensing key.
		);

		//disable doCheck(), to disable the licensing check
		// doCheck();

		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun())
			cl.getLogDialog().show();
		
		/*----------------category view-----------------------*/
		categoryView = (ListView) findViewById(R.id.tabThreeListView);
		
		adapter = new ArrayAdapter<String>(this,
				R.layout.category_list_item, listItems);
		
		categoryView.setAdapter(adapter);
		listItems.add("All");//0
		listItems.add("Applications");//1
		listItems.add("Network Tools");//2
		listItems.add("Root utilities");//3
		listItems.add("System Tools");//4
		listItems.add("Security");//5
		listItems.add("Tweaks");//6
		listItems.add("Themes");//7

		categoryView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent getAppListIntent = new Intent(FireplaceActivity.this,
						GetContentFromDBActivity.class);
				getAppListIntent.putExtra("position", position);
				startActivity(getAppListIntent);
			}
		});
		
		/*------------------------------------------------------*/
		
		File folder = new File("/sdcard/Fireplace/");

		if (folder.exists()) {
			String deleteCmd = "rm -r " + "/sdcard/Fireplace/";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}
		
		startupNetworkCheck();

		File fireplaceDir = new File("/sdcard/Fireplace/");
		fireplaceDir.mkdirs();

		mAppsList = (ListView) findViewById(R.id.listView1);
		mAppsList.setOnItemClickListener(this);

		mApps = loadInstalledApps(INCLUDE_SYSTEM_APPS);

		mAdapter = new AppListAdapter(getApplicationContext());
		
		mAdapter.setListItems(mApps);
		mAppsList.setAdapter(mAdapter);
		
		new LoadIconsTask().execute(mApps.toArray(new App[] {}));
				
/*-----------------------Unused-----------------------------------------/
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

		 TextView txtLoading = (TextView) findViewById(R.id.txtLoading);
		 txtLoading.setText("Setting up components"); // Initial loading
		
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
		
		 txtLoading.setText("Runnning network check");

		// create a dir
		 txtLoading.setText("Loading folders");

		 txtLoading.setText("All done!");

		 txtLoading.setVisibility(View.GONE);
		 ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);
		 pBar.setVisibility(View.GONE);

/------------------------------------------------------------------------------*/
	}

/*-------------------------------Licensing Area--------------------------------*/
	
	private void doCheck() {
		setProgressBarIndeterminateVisibility(true);
		mChecker.checkAccess(mLicenseCheckerCallback);
	}

	private void displayResult(final String result) {
		mHandler.post(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow() {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			// Should allow user access.
			displayResult(getString(R.string.allow));
		}

		public void dontAllow() {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			displayResult(getString(R.string.dont_allow));
			// Should not allow access. An app can handle as needed,
			// typically by informing the user that the app is not licensed
			// and then shutting down the app or limiting the user to a
			// restricted set of features.
			// In this example, we show a dialog that takes the user to Market.
			showDialog(0);
		}

		public void applicationError(ApplicationErrorCode errorCode) {
			// TODO Auto-generated method stub

		}
	}

/*--------------------------------Menu Options-----------------------------------*/
	
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
					+ "\nStian Insteb�, Sachira Chinthana Jayasanka,"
					+ "\nSimon Ponder & Zachary Spong");
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
//			try {
//				// set the download URL, a url that points to a file on the
//				// internet
//				// this is the file to be downloaded
//				URL url = new URL(
//						"http://www.fireplace-market.com/fireplaceUpdate.v"
//								+ getResources().getString(R.string.updateTo)
//								+ ".apk");
//
//				// create the new connection
//				HttpURLConnection urlConnection = (HttpURLConnection) url
//						.openConnection();
//
//				// set up some things on the connection
//				urlConnection.setRequestMethod("GET");
//				urlConnection.setDoOutput(true);
//
//				// and connect!
//				urlConnection.connect();
//
//				// set the path where we want to save the file
//				// in this case, going to save it on the root directory of the
//				// sd card.
//				File SDCardRoot = Environment.getExternalStorageDirectory();
//				// create a new file, specifying the path, and the filename
//				// which we want to save the file as.
//				File file = new File(SDCardRoot + "Fireplace/", "update.apk");
//
//				// this will be used to write the downloaded data into the file
//				// we created
//				FileOutputStream fileOutput = new FileOutputStream(file);
//
//				// this will be used in reading the data from the internet
//				InputStream inputStream = urlConnection.getInputStream();
//
//				// this is the total size of the file
//				int totalSize = urlConnection.getContentLength();
//				// variable to store total downloaded bytes
//				int downloadedSize = 0;
//
//				// create a buffer...
//				byte[] buffer = new byte[1024];
//				int bufferLength = 0; // used to store a temporary size of the
//										// buffer
//
//				// now, read through the input buffer and write the contents to
//				// the file
//				while ((bufferLength = inputStream.read(buffer)) > 0) {
//					// add the data in the buffer to the file in the file output
//					// stream (the file on the sd card
//					fileOutput.write(buffer, 0, bufferLength);
//					// add up the size so we know how much is downloaded
//					downloadedSize += bufferLength;
//					// this is where you would do something to report the
//					// prgress, like this maybe
//					updateProgress(downloadedSize, totalSize);
//
//				}
//				// close the output stream when done
//				fileOutput.close();
//
//				Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(
//						Uri.parse(SDCardRoot + "Fireplace/update.apk"))
//						.setType("application/vnd.android.package-archive");
//				startActivity(promptInstall);
//
//				// catch some possible errors...
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			// see
//			// http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
//
//			Toast.makeText(this, "No new updates available!", Toast.LENGTH_LONG)
//					.show();
//			break;
		}

		return true;

	}
	
	public void updateProgress(int currentSize, int totalSize) {
		// Toast.makeText(this, "Packages: " +
		// Long.toString((currentSize/totalSize)*100)+"% Complete",
		// Toast.LENGTH_SHORT).show();
	}
	
/*-----------------------------Various Other Click controls---------------------------*/

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.twiiterCon: // Twitter Button
			Intent browsetwitter = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://twitter.com/#!/FireplaceMarket"));
			startActivity(browsetwitter);
			break;

		case R.id.fbCon: // Facebook Button
			Intent browseFacebook = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://www.facebook.com/pages/Fireplace-Market/379268035417930"));
			startActivity(browseFacebook);

			break;
			
		case R.id.googleCon: //Google+ Button
			Intent browseGplus = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://plus.google.com/106118854945132150428"));
				startActivity(browseGplus);
				
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
			isOnline();
			break;
		}
	}
	
/*------------------------------Network Checking---------------------------------------*/

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Toast.makeText(FireplaceActivity.this, "Loading apps...",
					Toast.LENGTH_LONG).show();

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
	
/*-------------------------------Dialog Creation--------------------------------*/

	protected Dialog onCreateDialog(int id) {
		// We have only one dialog.
		return new AlertDialog.Builder(this)
				.setTitle(R.string.unlicensed_dialog_title)
				.setMessage(R.string.unlicensed_dialog_body)
				.setPositiveButton(R.string.buy_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent marketIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://market.android.com/details?id="
												+ getPackageName()));
								startActivity(marketIntent);
							}
						})
				.setNegativeButton(R.string.quit_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).create();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mChecker.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		viewFlow.onConfigurationChanged(newConfig);
	}

/*------------------------------installed apps methods--------------------------------*/	
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		final App app = (App) parent.getItemAtPosition(position);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		String msg = app.getTitle()
				+ "\n\n"
				+ "Version "
				+ app.getVersionName()
				+ " ("
				+ app.getVersionCode()
				+ ")"
				+ (app.getDescription() != null ? ("\n\n" + app
						.getDescription()) : "");

		Drawable icon = (iconsLoaded) ? mAdapter.getIcons().get(
				app.getPackageName()) : getResources().getDrawable(
				R.drawable.icon);

		builder.setMessage(msg)
				.setCancelable(true)
				.setTitle(app.getTitle())
				.setIcon(icon)
				.setPositiveButton("Launch",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// start the app by invoking its launch intent
								Intent i = getPackageManager()
										.getLaunchIntentForPackage(
												app.getPackageName());
								try {
									if (i != null) {
										startActivity(i);
									} else {
										i = new Intent(app.getPackageName());
										startActivity(i);
									}
								} catch (ActivityNotFoundException err) {
									Toast.makeText(FireplaceActivity.this,
											"Error launching app",
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton("Remove",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								Uri packageURI = Uri.parse("package:"
										+ app.getPackageName());
								Intent uninstallIntent = new Intent(
										Intent.ACTION_DELETE, packageURI);
								startActivity(uninstallIntent);
								dialog.cancel();
							}
						});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private List<App> loadInstalledApps(boolean includeSysApps) {
		List<App> apps = new ArrayList<App>();

		// the package manager contains the information about all installed apps
		PackageManager packageManager = getPackageManager();

		List<PackageInfo> packs = packageManager.getInstalledPackages(0); // PackageManager.GET_META_DATA

		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			ApplicationInfo a = p.applicationInfo;
			// skip system apps if they shall not be included
			if ((!includeSysApps)
					&& ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
				continue;
			}
			App app = new App();
			app.setTitle(p.applicationInfo.loadLabel(packageManager).toString());
			app.setPackageName(p.packageName);
			app.setVersionName(p.versionName);
			app.setVersionCode(p.versionCode);
			CharSequence description = p.applicationInfo
					.loadDescription(packageManager);
			app.setDescription(description != null ? description.toString()
					: "");
			apps.add(app);
		}
		return apps;
	}
	
	/**
	 * An asynchronous task to load the icons of the installed applications.
	 */
	private class LoadIconsTask extends AsyncTask<App, Void, Void> {
		@Override
		protected Void doInBackground(App... apps) {

			Map<String, Drawable> icons = new HashMap<String, Drawable>();
			PackageManager manager = getApplicationContext()
					.getPackageManager();

			for (App app : apps) {
				String pkgName = app.getPackageName();
				Drawable ico = null;
				try {
					Intent i = manager.getLaunchIntentForPackage(pkgName);
					if (i != null) {
						ico = manager.getActivityIcon(i);
					}
				} catch (NameNotFoundException e) {
					Log.e("ERROR", "Unable to find icon for package '"
							+ pkgName + "': " + e.getMessage());
				}
				icons.put(app.getPackageName(), ico);
			}
			mAdapter.setIcons(icons);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			iconsLoaded = true;
			mAdapter.notifyDataSetChanged();
		}
	}
	
/*---------------------------Saving View Location--------------------------*/	
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("CurrentView", viewFlow.getCurrentView());
		super.onSaveInstanceState(outState);
	}
	
/*-----------------------------Currently Unused-------------------------------------/

	 public boolean updateCheckNetwork() {
	 ConnectivityManager cm = (ConnectivityManager)
	 getSystemService(Context.CONNECTIVITY_SERVICE);
	 NetworkInfo netInfo = cm.getActiveNetworkInfo();
	 if (netInfo != null && netInfo.isConnectedOrConnecting()) {
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
	
/-------------------------------------------------------------------------*/
}