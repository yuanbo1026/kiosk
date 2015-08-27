package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.VideoView;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;

/**
 * Created by b.yuan on 05.08.2015.
 */
public class VideoActivity extends Activity {
	private Context context;
	private VideoView myVideoView;
	private int position = 0;
	private ProgressDialog progressDialog;
	private MediaController mediaControls;
	private String url;
	private String filename;
	private boolean isVideoDownloaded;
	private FileStorageHelper fileHelper;

	/*@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		View decorView = getWindow().getDecorView();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			if (hasFocus) {
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		}
	}*/

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_view);
		context = this;
		Intent intent = getIntent();
		url = intent.getStringExtra(getString(R.string
				.video_activity_intent_url_extra));
		filename = intent.getStringExtra("filename");
		isVideoDownloaded = intent.getBooleanExtra("isVideoDownloaded", false);

		fileHelper = new FileStorageHelper(this);

		if (mediaControls == null) {
			mediaControls = new MediaController(this);
		}
		if (!url.isEmpty() && !url.contains(getString(R.string.video_url_keywork))) {
			myVideoView = (VideoView) findViewById(R.id.video_view);

			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Playing Video...");
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			/*myVideoView.setVideoURI(Uri.parse("https://nexxoo:wenexxoo4kiosk!@www.appstock" +
					".de/kiosk/content/3/5/TechniTwin ISIO_Produktvideo.mp4"));*/
			try {
				myVideoView.setMediaController(mediaControls);
				if (isVideoDownloaded) {
					File temp = new File(fileHelper.getFileAbsolutePath(filename));
					if(temp.exists()){
						myVideoView.setVideoPath(fileHelper.getFileAbsolutePath(filename));
						Log.e(Nexxoo.TAG, fileHelper.getFileAbsolutePath(filename)+" " +
								"exists");
					}else{
						Log.e(Nexxoo.TAG,fileHelper.getFileAbsolutePath(filename)+" " +
								"doesn't exist.");
					}

				}else
					myVideoView.setVideoURI(Uri.parse(url));
				myVideoView.requestFocus();
				myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					// Close the progress bar and play the video
					public void onPrepared(MediaPlayer mp) {
						progressDialog.dismiss();
						myVideoView.seekTo(position);
						if (position == 0) {
							myVideoView.start();
						} else {
							myVideoView.pause();
						}
					}
				});


			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			/*try {
				String tmp = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
				Cache cache = new FileCache(new File(getExternalCacheDir(), name));
				HttpUrlSource source = new HttpUrlSource(tmp);
				HttpProxyCache proxyCache = new HttpProxyCache(source, cache);
				proxyCache.setCacheListener(new CacheListener() {
					@Override
					public void onError(ProxyCacheException e) {
						Log.e(Nexxoo.TAG, "Error playing video", e);
					}

					@Override
					public void onCacheDataAvailable(int cachePercentage) {
					}
				});
				myVideoView.setMediaController(mediaControls);
				myVideoView.setVideoPath(proxyCache.getUrl());
				myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					// Close the progress bar and play the video
					public void onPrepared(MediaPlayer mp) {
						progressDialog.dismiss();
						myVideoView.seekTo(position);
						if (position == 0) {
							myVideoView.start();
						} else {
							myVideoView.pause();
						}
					}
				});
			} catch (ProxyCacheException e) {
				// do nothing. onError() handles all errors
			}*/

		} else {
			Toast.makeText(context, getString(R.string.video_empty_url_alert_text), Toast.LENGTH_LONG).show();
		}


	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
		myVideoView.pause();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		position = savedInstanceState.getInt("Position");
		myVideoView.seekTo(position);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return true;
	}
}