package com.fireplace.activity;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.fireplace.adsup.R;

public class FireplacePreferenceActivity extends PreferenceActivity {
	
	private final static String TAG = "FireplacePreferenceActivity";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference repoPref = (Preference) findPreference("repoPref");
		repoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent intentRepo = new Intent(FireplacePreferenceActivity.this, RepoActivity.class);
				startActivityForResult(intentRepo, 0);
				return true;
			}
		});
		
		Preference updatePref = (Preference) findPreference("updatePref");
		updatePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				// try {
				// // set the download URL, a url that points to a file on the
				// // internet
				// // this is the file to be downloaded
				// URL url = new URL(
				// "http://www.fireplace-market.com/fireplaceUpdate.v"
				// + getResources().getString(R.string.updateTo)
				// + ".apk");
				//
				// // create the new connection
				// HttpURLConnection urlConnection = (HttpURLConnection) url
				// .openConnection();
				//
				// // set up some things on the connection
				// urlConnection.setRequestMethod("GET");
				// urlConnection.setDoOutput(true);
				//
				// // and connect!
				// urlConnection.connect();
				//
				// // set the path where we want to save the file
				// // in this case, going to save it on the root directory of the
				// // sd card.
				// File SDCardRoot = Environment.getExternalStorageDirectory();
				// // create a new file, specifying the path, and the filename
				// // which we want to save the file as.
				// File file = new File(SDCardRoot + "Fireplace/", "update.apk");
				//
				// // this will be used to write the downloaded data into the file
				// // we created
				// FileOutputStream fileOutput = new FileOutputStream(file);
				//
				// // this will be used in reading the data from the internet
				// InputStream inputStream = urlConnection.getInputStream();
				//
				// // this is the total size of the file
				// int totalSize = urlConnection.getContentLength();
				// // variable to store total downloaded bytes
				// int downloadedSize = 0;
				//
				// // create a buffer...
				// byte[] buffer = new byte[1024];
				// int bufferLength = 0; // used to store a temporary size of the
				// // buffer
				//
				// // now, read through the input buffer and write the contents to
				// // the file
				// while ((bufferLength = inputStream.read(buffer)) > 0) {
				// // add the data in the buffer to the file in the file output
				// // stream (the file on the sd card
				// fileOutput.write(buffer, 0, bufferLength);
				// // add up the size so we know how much is downloaded
				// downloadedSize += bufferLength;
				// // this is where you would do something to report the
				// // prgress, like this maybe
				// updateProgress(downloadedSize, totalSize);
				//
				// }
				// // close the output stream when done
				// fileOutput.close();
				//
				// Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(
				// Uri.parse(SDCardRoot + "Fireplace/update.apk"))
				// .setType("application/vnd.android.package-archive");
				// startActivity(promptInstall);
				//
				// // catch some possible errors...
				// } catch (MalformedURLException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// see
				// http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
				Toast.makeText(FireplacePreferenceActivity.this, "No new updates available!", Toast.LENGTH_LONG)
				.show();
				return true;
			}
		});
		
		Preference cachePref = (Preference) findPreference("cachePref");
		cachePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				File folder = new File("/sdcard/Fireplace/");

				if (folder.exists()) {
					String deleteCmd = "rm -r " + "/sdcard/Fireplace/";
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec(deleteCmd);
					} catch (IOException e) {
						Log.e(TAG, "In /sdcard/Fireplace/ folder removal", e);
					}
					
					folder.mkdirs();
					Toast.makeText(FireplacePreferenceActivity.this, "Cache has been cleared.", Toast.LENGTH_LONG)
					.show();
				} else {
					folder.mkdirs();
					Log.w(TAG, "Fireplace directory did not exist, so one was created on sdcard partition.");
				}
				
				return true;
			}
		});
		
		Preference donatePref = (Preference) findPreference("donatePref");
		donatePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent browse = new Intent(
						Intent.ACTION_VIEW,	Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVT4SNRTBSJCU"));
				if (hasGoodNetwork()) {
					startActivity(browse);
				}					
				return true;
			}
		});
		
		Preference aboutPref = (Preference) findPreference("aboutPref");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(FireplacePreferenceActivity.this);
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
							}
						});
	
				// show it
				alertbox.show();
				return true;
			}
		});
		
	}
	
	public boolean hasGoodNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		Toast.makeText(FireplacePreferenceActivity.this,
				"No network connection detected!", Toast.LENGTH_LONG).show();
		return false;
	}

}
