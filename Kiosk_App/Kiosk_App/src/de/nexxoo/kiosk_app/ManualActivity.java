package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by b.yuan on 05.08.2015.
 */
public class ManualActivity extends Activity implements View.OnClickListener{

	private ProgressBar progressBar;
	private String filepath = "KioskFileStorage";
	private File directory;
	private TextView index;
	private Button download;
	private Button open;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual_view);

		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);

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
	/*		case R.id.start:
				progressBar1.setVisibility(View.VISIBLE);
				break;

			case R.id.stop:
				progressBar1.setVisibility(View.GONE);
				break;*/

			case R.id.manual_view_button_download:
				String url = "http://upload.wikimedia.org/wikipedia/commons/0/05/Sna_large.png";
				grabURL(url);
				break;

			case R.id.manual_view_button_open:
				break;

		}
	}

	public void grabURL(String url) {
		new GrabURL().execute(url);
	}

	private class GrabURL extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(0);
		}

		protected String doInBackground(String... urls) {
			String filename = "KioskSampleFile.png";
			File myFile = new File(directory , filename);

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

			return filename;

		}

		protected void onProgressUpdate(Integer... progress) {
			index.setVisibility(View.VISIBLE);
			index.setText(String.valueOf(progress[0]) + " % ");
		}

		protected void onCancelled() {
			Toast toast = Toast.makeText(getBaseContext(),
					"Error connecting to Server", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();

		}

		protected void onPostExecute(String filename) {
			index.setVisibility(View.VISIBLE);
			index.setText("Finished downloading...");
			File myFile = new File(directory , filename);
			ImageView myImage = (ImageView) findViewById(R.id.manual_view_cover);
			myImage.setImageBitmap(BitmapFactory.decodeFile(myFile.getAbsolutePath()));
		}

	}
}