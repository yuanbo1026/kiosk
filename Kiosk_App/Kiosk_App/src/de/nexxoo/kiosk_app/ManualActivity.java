package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by b.yuan on 05.08.2015.
 */
public class ManualActivity extends Activity implements View.OnClickListener {

	private static final String BASICAUTH = "nexxoo:wenexxoo4kiosk!";

	private ProgressBar progressBar;
	//	private String filepath = "downloads";
//	private String fileAbsolutePath;
//	String filename = "test.pdf";
	//	private File directory;

	private String filename;
	private String url;

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

		Intent intent = getIntent();
		filename = intent.getStringExtra("filename");
		url = intent.getStringExtra("url");

		File folder = new File(fileStorageHelper.getDownloadFolder());
		if (!folder.exists())
			folder.mkdirs();
//		fileAbsolutePath = fileStorageHelper.getFileAbsolutePath(filename);
//		Log.d("fileAbsolutePath", fileAbsolutePath);


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

//				NexxooHttpJsonRequest request = new NexxooHttpJsonRequest
//						(Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP));
//				request.performDownloadJsonRequest("https://www.appstock" +
//						".de/kiosk/content/1/4/bedanl_octophonF203040_key%20module_an%20octopusE300_800.pdf");

				if (fileStorageHelper.isContentDownloaded(filename)) {
					Toast.makeText(this, "Already Downloaded", Toast.LENGTH_LONG).show();
				} else {
//					String url = "http://upload.wikimedia.org/wikipedia/commons/0/05/Sna_large.png";
//					String url = "https://www.appstock" +
//							".de/kiosk/content/1/4/bedanl_octophonF203040_key%20" +
//							"module_an%20octopusE300_800.pdf";
					grabURL(url);
				}
				/*FileStorageHelper.download(new BaseEntity(4,"Test1","bedanl_octophonF203040_key module_an octopusE300_800.pdf"), this, new
						OnDownloadResult() {
					@Override
					public void onDownloadStarted(BaseEntity content) {
						progressBar.setVisibility(View.VISIBLE);
						progressBar.setProgress(0);
						index.setVisibility(View.VISIBLE);
						index.setText(" Start Downloading... ");
					}

					@Override
					public void onDownloadSuccess(BaseEntity content) {
						index.setVisibility(View.GONE);
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onDownloadProgress(int percent) {
						index.setText(String.valueOf(percent) + " % ");
						progressBar.setProgress(percent);
					}

					@Override
					public void onDownloadFailed(String msg, int code) {

					}
				});*/
				break;

			case R.id.manual_view_button_open:
				if(fileStorageHelper.isContentDownloaded(filename)){
					File file = new File(fileStorageHelper.getFileAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file),"application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent intent = Intent.createChooser(target, "Open File");
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						// Instruct the user to install a PDF reader here, or something
					}
				}else{
					Toast.makeText(this,"There is no such PDF file.",Toast.LENGTH_LONG)
							.show();
				}

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
			File myFile = new File(fileStorageHelper.getFileAbsolutePath(filename));
			InputStream input = null;
			OutputStream output = null;

			Log.d(Nexxoo.TAG,urls[0]);

			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("User-Agent","nexxoo");
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Log.d("Kiosk Error", "connection.getResponseCode:" + connection
							.getResponseCode());
				}

				int fileLength = connection.getContentLength();

				// download the file
				FileStorageHelper helper = new FileStorageHelper(context);

				input = connection.getInputStream();

				File folder = new File(helper.getDownloadFolder());
				if (!folder.exists())
					folder.mkdirs();

				output = new FileOutputStream(fileStorageHelper.getFileAbsolutePath(filename));

				byte data[] = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						Log.e("ASYNC", "canceled somehow");
						//mCallback.onDownloadFailed(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_DOWNLOAD_DLCANCELED), JSONErrorHandler.WS_ERROR_DOWNLOAD_DLCANCELED);
						return "";
					}
					total += count;
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
						publishProgress((int) (total * 100 / fileLength));

					output.write(data, 0, count);
//                Log.d(Nexxoo.TAG, "Filedownload: Wrote " + count + "bytes");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return fileStorageHelper.getFileAbsolutePath(filename);

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

		@Override
		protected void onPostExecute(String filename) {
			index.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);

			/**
			 * display PDF file
			 */
			File file = new File(filename);
			Intent target = new Intent(Intent.ACTION_VIEW);
			target.setDataAndType(Uri.fromFile(file),"application/pdf");
			target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			Intent intent = Intent.createChooser(target, "Open File");
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				// Instruct the user to install a PDF reader here, or something
			}

			/*File myFile = new File(filename);
			ImageView myImage = (ImageView) findViewById(R.id.manual_view_cover);
			myImage.setImageBitmap(BitmapFactory.decodeFile(myFile.getAbsolutePath()));*/
		}

	}
}