package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by b.yuan on 05.08.2015.
 */
public class ManualActivity extends Activity implements View.OnClickListener {

	private ProgressBar progressBar;
//	private String filepath = "downloads";
	private String fileAbsolutePath;
	String filename = "sample.jpg";
//	private File directory;
	private TextView index;
	private Button download;
	private Button open;
	private Context context;
	private FileStorageHelper fileStorageHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual_view);

		context = this;
		fileStorageHelper = new FileStorageHelper(this);
		File folder = new File(fileStorageHelper.getDownloadFolder() );
		if (!folder.exists())
			folder.mkdirs();
		fileAbsolutePath = fileStorageHelper.getFileAbsolutePath(filename);
		Log.d("fileAbsolutePath", fileAbsolutePath);


		progressBar = (ProgressBar) findViewById(R.id.manual_view_progressBar);
		progressBar.setVisibility(View.GONE);
		index = (TextView) findViewById(R.id.manual_view_progress_index);
		index.setVisibility(View.GONE);

		download = (Button) findViewById(R.id.manual_view_button_download);
		download.setOnClickListener(this);
		open = (Button) findViewById(R.id.manual_view_button_open);
		open.setOnClickListener(this);
	}

	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.manual_view_button_download:

				if (fileStorageHelper.isContentDownloaded(filename)) {
					Toast.makeText(this,"Already Downloaded",Toast.LENGTH_LONG).show();
				}else{
					String url = "http://www.101apps.co.za/images/headers/101_logo_very_small.jpg";
					grabURL(url);
				}

				break;

			case R.id.manual_view_button_open:
				Intent intent = new Intent(this, DownloadActivity.class);
				startActivity(intent);
				break;

		}
	}

	/**
	 * download async task
	 *
	 * @param url
	 */
	public void grabURL(String url) {
		new GrabURL().execute(url);
	}

	private class GrabURL extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(0);
			index.setVisibility(View.VISIBLE);
			index.setText(" Start Downloading... ");
		}

		protected String doInBackground(String... urls) {
			File myFile = new File(fileAbsolutePath);


			try {
				URL url = new URL(urls[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileSize = connection.getContentLength();

				InputStream is = new BufferedInputStream(url.openStream());
				OutputStream os = new FileOutputStream(myFile);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = is.read(data)) != -1) {
					total += count;
					publishProgress((int) (total * 100 / fileSize));
					os.write(data, 0, count);
				}

				os.flush();
				os.close();
				is.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return fileAbsolutePath;

		}

		protected void onProgressUpdate(Integer... progress) {
			index.setText(String.valueOf(progress[0]) + " % ");
			progressBar.setProgress(progress[0]);
		}

		protected void onCancelled() {
			Toast toast = Toast.makeText(getBaseContext(),
					"Error connecting to Server", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();

		}

		protected void onPostExecute(String filename) {
			index.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			File myFile = new File(filename);
			ImageView myImage = (ImageView) findViewById(R.id.manual_view_cover);
			myImage.setImageBitmap(BitmapFactory.decodeFile(myFile.getAbsolutePath()));
		}

	}
}