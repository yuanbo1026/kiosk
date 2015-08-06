package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by b.yuan on 04.08.2015.
 *
 * For test download function, downloading a pic and display
 */
public class DownloadActivity extends Activity {
	DownloadManager downloadManager;
	String downloadFileUrl = "http://www.101apps.co.za/" +
			"images/headers/101_logo_very_small.jpg";
	private long myDownloadReference;
	private BroadcastReceiver receiverDownloadComplete;
	private BroadcastReceiver receiverNotificationClicked;

	private String TAG = "download";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		Button buttonCheckNetwork = (Button) findViewById(R.id.button2);
		buttonCheckNetwork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConnectivityManager conManager = (ConnectivityManager)
						getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = conManager.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null
						&& activeNetwork.isConnectedOrConnecting();

				if (isConnected) {
					boolean isWiFi = activeNetwork.getType() == conManager.TYPE_WIFI;
					boolean isMobile = activeNetwork.getType() == conManager.TYPE_MOBILE;
					if (isWiFi) {
						Toast.makeText(DownloadActivity.this, "Connected via WiFi",
								Toast.LENGTH_SHORT).show();
					} else if (isMobile) {
						Toast.makeText(DownloadActivity.this, "Connected via Mobile",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(DownloadActivity.this, "No Connection",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

		Button buttonDownload = (Button) findViewById(R.id.button);
		buttonDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Uri uri = Uri.parse(downloadFileUrl);
				DownloadManager.Request request = new DownloadManager.Request(uri);

//                set the notification
				request.setDescription("My Download")
						.setTitle("Notification Title");

//                set the path to where to save the file
//                      save in app package directory
				request.setDestinationInExternalFilesDir(DownloadActivity.this,
						Environment.DIRECTORY_DOWNLOADS, "logo.jpg");
//                      save in the public downloads folder
//                request.setDestinationInExternalPublicDir(Environment.
//                              DIRECTORY_DOWNLOADS, "MyWebsiteLogo.png");

//                make file visible by and manageable by system's download app
				request.setVisibleInDownloadsUi(true);

//                select which network, etc
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
						| DownloadManager.Request.NETWORK_MOBILE);

//                queue the download
				myDownloadReference = downloadManager.enqueue(request);
			}
		});


	}

	@Override
	protected void onResume() {
		super.onResume();

//        filter for notifications - only acts on notification
//              while download busy
		IntentFilter filter = new IntentFilter(DownloadManager
				.ACTION_NOTIFICATION_CLICKED);

		receiverNotificationClicked = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String extraId = DownloadManager
						.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
				long[] references = intent.getLongArrayExtra(extraId);
				for (long reference : references) {
					if (reference == myDownloadReference) {
//                        do something with the download file
					}
				}
			}
		};
		registerReceiver(receiverNotificationClicked, filter);

//        filter for download - on completion
		IntentFilter intentFilter = new IntentFilter(DownloadManager
				.ACTION_DOWNLOAD_COMPLETE);

		receiverDownloadComplete = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				long reference = intent.getLongExtra(DownloadManager
						.EXTRA_DOWNLOAD_ID, -1);
				if (myDownloadReference == reference) {
//                    do something with the download file
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(reference);
					Cursor cursor = downloadManager.query(query);

					cursor.moveToFirst();
//                        get the status of the download
					int columnIndex = cursor.getColumnIndex(DownloadManager
							.COLUMN_STATUS);
					int status = cursor.getInt(columnIndex);

					int fileNameIndex = cursor.getColumnIndex(DownloadManager
							.COLUMN_LOCAL_FILENAME);
					String savedFilePath = cursor.getString(fileNameIndex);

//                        get the reason - more detail on the status
					int columnReason = cursor.getColumnIndex(DownloadManager
							.COLUMN_REASON);
					int reason = cursor.getInt(columnReason);

					switch (status) {
						case DownloadManager.STATUS_SUCCESSFUL:

//                                start activity to display the downloaded image
							Intent intentDisplay = new Intent(DownloadActivity.this,
									DisplayActivity.class);
							intentDisplay.putExtra("uri", savedFilePath);
							startActivity(intentDisplay);

							break;
						case DownloadManager.STATUS_FAILED:
							Toast.makeText(DownloadActivity.this,
									"FAILED: " + reason,
									Toast.LENGTH_LONG).show();
							break;
						case DownloadManager.STATUS_PAUSED:
							Toast.makeText(DownloadActivity.this,
									"PAUSED: " + reason,
									Toast.LENGTH_LONG).show();
							break;
						case DownloadManager.STATUS_PENDING:
							Toast.makeText(DownloadActivity.this,
									"PENDING!",
									Toast.LENGTH_LONG).show();
							break;
						case DownloadManager.STATUS_RUNNING:
							Toast.makeText(DownloadActivity.this,
									"RUNNING!",
									Toast.LENGTH_LONG).show();
							break;
					}
					cursor.close();
				}
			}
		};
		registerReceiver(receiverDownloadComplete, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiverDownloadComplete);
		unregisterReceiver(receiverNotificationClicked);
	}
}