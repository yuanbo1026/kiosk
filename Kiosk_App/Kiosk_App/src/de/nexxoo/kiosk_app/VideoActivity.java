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
		if (mediaControls == null) {
			mediaControls = new MediaController(this);
		}
		if (!url.isEmpty() && !url.contains(getString(R.string.video_url_keywork))) {
			// Find your VideoView in your video_main.xml layout
			myVideoView = (VideoView) findViewById(R.id.video_view);

			// Create a progressbar
			progressDialog = new ProgressDialog(this);
			// Set progressbar title
			progressDialog.setTitle("Playing Video...");
			// Set progressbar message
			progressDialog.setMessage("Loading...");

			progressDialog.setCancelable(false);
			// Show progressbar
			progressDialog.show();

			try {
				myVideoView.setMediaController(mediaControls);
			myVideoView.setVideoURI(Uri.parse(url));
				/*myVideoView.setVideoURI(Uri.parse("https://nexxoo:wenexxoo4kiosk!@www" +
						".appstock" +
						".de/kiosk/content/3/5/TechniTwin ISIO_Produktvideo.mp4"));*/
//			myVideoView.setVideoURI(Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"));

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

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