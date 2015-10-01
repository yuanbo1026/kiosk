package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
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
		myVideoView = (VideoView) findViewById(R.id.video_view);
		myVideoView.setMediaController(mediaControls);
		try {
			if (isVideoDownloaded) {
				File temp = new File(fileHelper.getDownloadAbsolutePath(filename));
				if (temp.exists()) {
					myVideoView.setVideoPath(fileHelper.getDownloadAbsolutePath(filename));
					Log.d(Nexxoo.TAG, fileHelper.getDownloadAbsolutePath(filename) + " " +
							"exists");
				} else {
					Log.e(Nexxoo.TAG, fileHelper.getDownloadAbsolutePath(filename) + " " +
							"doesn't exist.");
					new AlertDialog.Builder(VideoActivity.this)
							.setMessage(VideoActivity.this.getResources().getString(R.string.video_empty_url_alert_text))
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
				}
			} else {
				if (url != null && !url.isEmpty() && !url.contains(getString(R.string.video_url_keywork))) {
					myVideoView.setVideoURI(Uri.parse(url));
				} else {
					Toast.makeText(context, getString(R.string.video_empty_url_alert_text), Toast.LENGTH_LONG).show();
				}
			}
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Video abspielen");
			progressDialog.setMessage("Loading");
			progressDialog.setCancelable(false);
			progressDialog.show();
			myVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					new AlertDialog.Builder(VideoActivity.this)
							.setMessage(VideoActivity.this.getResources().getString(R.string.video_empty_url_alert_text))
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
					return true;
				}
			});
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
			finish();
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
}