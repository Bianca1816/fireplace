package com.fireplace.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.taptwo.android.widget.TitleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.fireplace.adapter.AppListAdapter;
import com.fireplace.adapter.MainViewAdapter;
import com.fireplace.receiver.AlarmReceiver;
import com.fireplace.software.App;
import com.fireplace.software.ChangeLog;
import com.fireplace.software.ParcelableHolder;
import com.fireplace.software.R;

public class FireplaceActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	private ViewFlow viewFlow;

	private ImageView googlePlusImageView, twitterImageView, facebookImageView,
			featuredAppImageView;
	private ListView categoryListView, installedAppsListView;
	private WebView featuredAppWebView;
	
	private Handler mHandler;
	private ProgressBar pBar;
	private ParcelableHolder pHolder = new ParcelableHolder();

	private List<App> installedAppsList;
	private ArrayList<String> categoryListItems = new ArrayList<String>();

	private ArrayAdapter<String> categoryAdapter;
	private AppListAdapter installedAppsAdapter;
	private MainViewAdapter mainViewAdapter;

	private static final boolean INCLUDE_SYSTEM_APPS = false;
	private final static String TAG = "FireplaceActivity";
	private final static String FEATURED_URL = "http://www.google.com";
	private Map<String, Drawable> icons = new HashMap<String, Drawable>();

	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlJkXAwlWqWf1GHzIl00JjPYrG6cSN2VJrNm8Df97yU+KsxhT51xmI8bilwJ1BQTPBWTJ0CMcoY79rGiA1rOTXEXQRSintOQ8TePGBD1NL1G0nxAf1gqd7TEvsTSGaCMlT4Cx0lXL6wsktVgdcoRN/Z6NV1ZRKRX2yz18pg8q2ps2UT5Ur4IKNRMSL0rQT17QyRpzhBkitwRcl8IsQDr1j8/rC5VjiF48QkSVDEQ5DPO+7amsGLSI9GrhCl+BR09t37Ko37erjCkJmzgAaoYH7czc8MlPhB2/EKFVcTeEYVw3gVYdYGBUUhGqxJ/RSXoBqIpdBIC4AB4wEO37wniVKwIDAQAB";
	private static final byte[] SALT = new byte[] { -26, 65, 30, -118, -113,
			-57, 74, -64, 53, 69, -95, -25, 71, -117, -36, -113, -11, 37, -64,
			49 };

	private LicenseCheckerCallback licenseCheckerCallback;
	private LicenseChecker licenseChecker;
	private Handler handler;

	private boolean iconsLoaded;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newviewflow);

		iconsLoaded = false;
		
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun())
			cl.getLogDialog().show();

		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mainViewAdapter = new MainViewAdapter(this);

		if (savedInstanceState == null) {
			viewFlow.setAdapter(mainViewAdapter, 1);
		} else {
			viewFlow.setAdapter(mainViewAdapter,
					savedInstanceState.getInt("CurrentView"));
			iconsLoaded = (boolean) savedInstanceState.getBoolean("iconsLoaded");
		}

		TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
		indicator.setTitleProvider(mainViewAdapter);
		viewFlow.setFlowIndicator(indicator);

		googlePlusImageView = (ImageView) findViewById(R.id.googleCon);
		twitterImageView = (ImageView) findViewById(R.id.twiiterCon);
		facebookImageView = (ImageView) findViewById(R.id.fbCon);
		featuredAppImageView = (ImageView) findViewById(R.id.featureTileTop);
		featuredAppWebView = (WebView) findViewById(R.id.webView);

		handler = new Handler();

		licenseCheckerCallback = new MyLicenseCheckerCallback();
		String deviceId = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID + Secure.LOGGING_ID);
		licenseChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
				new AESObfuscator(SALT, getPackageName(), deviceId)),
				BASE64_PUBLIC_KEY // Your public licensing key.
		);

		// disable doLicenseCheck(), to disable the licensing check
//		doLicenseCheck();

		/*----------------category view-----------------------*/
		categoryListView = (ListView) findViewById(R.id.tabThreeListView);
		categoryAdapter = new ArrayAdapter<String>(this,
				R.layout.category_list_item, categoryListItems);
		categoryListView.setAdapter(categoryAdapter);
		for (String categoryName : getResources().getStringArray(
				R.array.Category))
			categoryListItems.add(categoryName);
		categoryListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent getAppListIntent = new Intent(FireplaceActivity.this,
						GetContentLocalActivity.class);
				getAppListIntent.putExtra("position", position);
				if (hasGoodNetwork())
					startActivity(getAppListIntent);
			}
		});

		/*------------------------------------------------------*/

		File folder = new File("/sdcard/Fireplace/");

		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		mHandler = new Handler(){
			public void handleMessage(Message msg) { 
				pBar.setVisibility(View.GONE);
				if (!iconsLoaded) {
					installedAppsListView.setAdapter(installedAppsAdapter);
				} else {
					installedAppsAdapter.notifyDataSetChanged();
				}
				
		    }			
		};

		pBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
		pBar.setIndeterminate(true);

		installedAppsListView = (ListView) findViewById(R.id.listView1);
		installedAppsListView.setOnItemClickListener(this);
		installedAppsAdapter = new AppListAdapter(getApplicationContext());

		if (savedInstanceState != null && iconsLoaded) {
			pHolder = (ParcelableHolder) savedInstanceState.getParcelable("parcel");
			installedAppsList = (List<App>) pHolder.get("installedAppsList");
			icons = (HashMap<String, Drawable>) pHolder.get("icons");
			installedAppsAdapter.setListItems(installedAppsList);
			installedAppsAdapter.setIcons(icons);

			installedAppsListView.setAdapter(installedAppsAdapter);
			pBar.setVisibility(View.GONE);
		} else {		
			new LoadIconsTask().execute(installedAppsList);
		}

//		// -----------Decide to show static or dynamic content--------------
//		if (hasGoodNetwork()) {
//			featuredAppImageView.setVisibility(View.GONE);
//			googlePlusImageView.setVisibility(View.GONE);
//			twitterImageView.setVisibility(View.GONE);
//			facebookImageView.setVisibility(View.GONE);
//
//			featuredAppWebView.getSettings().setJavaScriptEnabled(true);
//			featuredAppWebView.setWebChromeClient(new WebChromeClient() {
//				public void onProgressChanged(WebView view, int progress) {
//					FireplaceActivity.this.setTitle("Loading...");
//					FireplaceActivity.this.setProgress(progress * 100);
//
//					if (progress == 100)
//						FireplaceActivity.this.setTitle(R.string.app_name);
//				}
//			});
//
//			featuredAppWebView.setWebViewClient(new WebViewClient() {
//				@Override
//				public void onReceivedError(WebView view, int errorCode,
//						String description, String failingUrl) {
//					// Handle the error
//				}
//
//				@Override
//				public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					view.loadUrl(url);
//					return true;
//				}
//			});
//			featuredAppWebView.loadUrl(FEATURED_URL);
//			featuredAppWebView.setOnClickListener(this);
//		} else {
			featuredAppWebView.setVisibility(View.GONE);
			googlePlusImageView.setImageResource(R.drawable.googleplus);
			twitterImageView.setImageResource(R.drawable.twitter);
			facebookImageView.setImageResource(R.drawable.fb);
			featuredAppImageView.setImageResource(R.drawable.su_ic_logo);

			googlePlusImageView.setOnClickListener(this);
			twitterImageView.setOnClickListener(this);
			facebookImageView.setOnClickListener(this);
			featuredAppImageView.setOnClickListener(this);
//		}

	}

	/*-------------------------------Licensing Area--------------------------------*/

	private void doLicenseCheck() {
		setProgressBarIndeterminateVisibility(true);
		licenseChecker.checkAccess(licenseCheckerCallback);
	}

	private void displayResult(final String result) {
		handler.post(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow() {
			if (isFinishing()) {
				return;
			}
			displayResult(getString(R.string.allow));
		}

		public void dontAllow() {
			if (isFinishing()) {
				return;
			}
			displayResult(getString(R.string.dont_allow));
			showDialog(0);
		}

		public void applicationError(ApplicationErrorCode errorCode) {
			dontAllow();
		}
	}

	@Override
	protected void onResume() {
		setRecurringAlarm(getApplicationContext());
		super.onResume();
	}
	
	/*--------------------Recurring Alarm for DB refresh-----------------------------*/
	
	private void setRecurringAlarm(Context context) {

	    Calendar updateTime = Calendar.getInstance();
	    updateTime.setTimeZone(TimeZone.getDefault());
	    updateTime.set(Calendar.HOUR_OF_DAY, new Random().nextInt(24));
	    updateTime.set(Calendar.MINUTE, new Random().nextInt(60));
	 
	    Intent sync = new Intent(context, AlarmReceiver.class);
	    PendingIntent recurringSync = PendingIntent.getBroadcast(context,
	            0, sync, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
	            updateTime.getTimeInMillis(),
	            AlarmManager.INTERVAL_DAY, recurringSync);
	}
	
	/*--------------------------------Menu Options-----------------------------------*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.prefOption :
			Intent prefIntent = new Intent(this, FireplacePreferenceActivity.class);
			startActivity(prefIntent);
		break;
		
		default:
			
		super.onOptionsItemSelected(item);
	}

	return true;
	
	}

	/*-----------------------------Various Other Click controls---------------------------*/

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.twiiterCon: // Twitter Button
			Intent browsetwitter = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://twitter.com/#!/FireplaceMarket"));
			if (hasGoodNetwork())
				startActivity(browsetwitter);
			break;

		case R.id.fbCon: // Facebook Button
			Intent browseFacebook = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://www.facebook.com/pages/Fireplace-Market/379268035417930"));
			if (hasGoodNetwork())
				startActivity(browseFacebook);

			break;

		case R.id.googleCon: // Google+ Button
			Intent browseGplus = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://plus.google.com/106118854945132150428"));
			if (hasGoodNetwork())
				startActivity(browseGplus);

			break;

		case R.id.featureTileTop: // Featured App (Static for this release, will
									// be dynamic next release)
			Intent featuredIntent = new Intent(FireplaceActivity.this,
					DownloadFileActivity.class);
			featuredIntent.putExtra("title", "Superuser");
			featuredIntent
					.putExtra(
							"desc",
							"Hook into your phone's power.\nGrant and manage Superuser rights for your phone.\n\nThis app requires that you already have root, or a custom recovery image to work.");
			featuredIntent.putExtra("devl", "ChansDD");
			featuredIntent.putExtra("icon", BitmapFactory.decodeResource(
					getResources(), R.drawable.su_icon));
			featuredIntent.putExtra("link",
					"http://www.fireplace-market.com/apks/superuser.apk");
			if (hasGoodNetwork())
				startActivity(featuredIntent);

			break;
			
		case R.id.webView:
			String urlVar = featuredAppWebView.getUrl().replace(
					FEATURED_URL + "/feat=", "");
			Intent webFeaturedIntent = new Intent(FireplaceActivity.this,
					DownloadFileActivity.class);
			webFeaturedIntent.putExtra("featuredAppId", urlVar);
			startActivity(webFeaturedIntent);
			break;

//		case R.id.btnStorage: // Storage button
//			// Show storage left on phone and SD card
//			final Context contextStorage = this;
//
//			Intent intentStorage = new Intent(contextStorage,
//					StorageActivity.class);
//			startActivityForResult(intentStorage, 0);
//			break;

		}
	}

	/*------------------------------Network Checking---------------------------------------*/

	public boolean hasGoodNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		Toast.makeText(FireplaceActivity.this,
				"No network connection detected!", Toast.LENGTH_LONG).show();
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
								if (hasGoodNetwork())
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
		licenseChecker.onDestroy();
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

		Drawable icon = (iconsLoaded) ? installedAppsAdapter.getIcons().get(
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
									Log.e(TAG,
											"In onItemClick for Installed Apps",
											err);
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

		PackageManager packageManager = getPackageManager();
		List<PackageInfo> packs = packageManager.getInstalledPackages(0); // PackageManager.GET_META_DATA

		for (PackageInfo packInfo : packs) {
			// skip system apps if they shall not be included
			if ((!includeSysApps)
					&& ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
				continue;
			}
			App app = new App();
			app.setTitle(packInfo.applicationInfo.loadLabel(packageManager)
					.toString());
			app.setPackageName(packInfo.packageName);
			app.setVersionName(packInfo.versionName);
			app.setVersionCode(packInfo.versionCode);
			CharSequence description = packInfo.applicationInfo
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
	private class LoadIconsTask extends AsyncTask<List<App>, Void, Void> {
		@Override
		protected Void doInBackground(List<App>... apps) {

			installedAppsList = (apps[0] != null && apps[0].size() > 0) 
					? apps[0]
					: loadInstalledApps(INCLUDE_SYSTEM_APPS);
			installedAppsAdapter.setListItems(installedAppsList);
			mHandler.sendEmptyMessage(0);
			
			icons = new HashMap<String, Drawable>();
			PackageManager manager = getApplicationContext()
					.getPackageManager();

			for (App app : installedAppsList) {
				String pkgName = app.getPackageName();
				Drawable ico = null;
				try {
					Intent i = manager.getLaunchIntentForPackage(pkgName);
					if (i != null) {
						ico = manager.getActivityIcon(i);
					}
				} catch (NameNotFoundException e) {
					Log.e("ERROR", "Unable to find icon for package '"
							+ pkgName, e);
				}
				icons.put(app.getPackageName(), ico);
			}
			installedAppsAdapter.setIcons(icons);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			iconsLoaded = true;
			mHandler.sendEmptyMessage(0);
		}
	}

	/*---------------------------Saving View Location--------------------------*/

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("CurrentView", viewFlow.getCurrentView());
		pHolder.put("installedAppsList", installedAppsList);
		pHolder.put("icons", icons);
		outState.putParcelable("parcel", pHolder);
		outState.putBoolean("iconsLoaded", iconsLoaded);
		super.onSaveInstanceState(outState);
	}

}