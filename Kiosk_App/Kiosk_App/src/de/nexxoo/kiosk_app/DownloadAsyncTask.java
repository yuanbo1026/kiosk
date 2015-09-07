package de.nexxoo.kiosk_app;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by b.yuan on 25.08.2015.
 */
public class DownloadAsyncTask extends AsyncTask<Void, Integer, String> {
	private static final String BASICAUTH = "nexxoo:wenexxoo4kiosk!";
	private ProgressDialog progressDialog;
	private Context mContext;
	private String mUrl;
	private String filename;
	private FileStorageHelper fileStorageHelper;
	private String basicAuthBase64;
	private int entityType = -1;
	private ImageView mImageView;
	private DownloadCallback mCallnack;


	public DownloadAsyncTask(Context context, String url, String filename) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
//			Log.e("BASIC", basicAuthBase64);
		}
	}


	public DownloadAsyncTask(Context context, String url, String filename, int entityType) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
//			Log.e("BASIC", basicAuthBase64);
		}
	}
	public DownloadAsyncTask(Context context, String url, String filename, int entityType, DownloadCallback callback) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
//			Log.e("BASIC", basicAuthBase64);
		}
		this.mCallnack = callback;
	}
	/**
	 * This method is not useful anymore, there is not need to change grid view item
	 * icon to trust image
	 */
	public DownloadAsyncTask(Context context, String url, String filename, ImageView
			mImageView, int entityType) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		this.mImageView = mImageView;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
//			Log.e("BASIC", basicAuthBase64);
		}
	}

	protected void onPreExecute() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(mContext.getString(R.string.download_task_message));
		progressDialog.setProgressNumberFormat(null);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.show();

		fileStorageHelper = new FileStorageHelper(mContext);
		File folder = new File(fileStorageHelper.getDownloadFolder());
		if (!folder.exists())
			folder.mkdirs();

		if (fileStorageHelper.isContentDownloaded(filename)) {
			Toast.makeText(mContext, "Already Downloaded", Toast.LENGTH_LONG).show();
			onPostExecute(filename);
		}
	}

	protected String doInBackground(Void... urls) {
		File myFile = new File(fileStorageHelper.getFileAbsolutePath(filename));
		InputStream input = null;
		OutputStream output = null;
//		Log.e(Nexxoo.TAG, "Video FileName is " + filename);
		FileStorageHelper helper = new FileStorageHelper(mContext);
		try {
			URL url = new URL(mUrl);
//			Log.e(Nexxoo.TAG, "URL: " + mUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", basicAuthBase64);
			connection.setRequestProperty("Accept-Encoding", "identity");
			if (entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_PDF)
				connection.setRequestProperty("User-Agent", "nexxoo");
			connection.setDoInput(true);
			connection.setDoOutput(true);
//			Log.e(Nexxoo.TAG, connection.getHeaderFields().toString());
			connection.connect();


			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(Nexxoo.TAG, "HttpURLConnection: " + connection.getResponseCode());
				Log.e(Nexxoo.TAG, connection.getErrorStream().toString());
			}

			int fileLength = connection.getContentLength();
//			Log.e(Nexxoo.TAG, "fileLength: " + fileLength);
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
			Log.e(Nexxoo.TAG, "Exception: " + e.getMessage());
		}

		return fileStorageHelper.getFileAbsolutePath(filename);

	}

	protected void onProgressUpdate(Integer... progress) {
		progressDialog.setProgress(progress[0]);
	}

	protected void onCancelled() {
		Toast toast = Toast.makeText(mContext,
				"Error connecting to Server", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 25, 400);
		toast.show();

	}

	@Override
	protected void onPostExecute(String filename) {
		progressDialog.dismiss();
		/**
		 * no need anymore, backup for later stage.
		 */
		if (mImageView != null && mImageView instanceof ImageView) {
			ImageView image = (ImageView) mImageView;
			image.setImageResource(R.drawable.ic_grid_trash);
			image.setVisibility(View.VISIBLE);
		}
		/**
		 * There is no effect to change to swipe menu item icon.
		 */
		/*if (menu != null && view instanceof ImageView) {
			menu.getMenuItem(0).setIcon(R.drawable.ic_list_trash);
		}*/


//		Log.e(Nexxoo.TAG, "fileStorageHelper.getFileAbsolutePath(filename): " + filename);
		/**
		 * display PDF file
		 */
		if (entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_PDF) {
			File file = new File(filename);
			Intent target = new Intent(Intent.ACTION_VIEW);
			target.setDataAndType(Uri.fromFile(file), "application/pdf");
			target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			Intent intent = Intent.createChooser(target, "Open File");
			try {
				mContext.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				// Instruct the user to install a PDF reader here, or something
			}
		}else if(entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_VIDEO){

		}

		if(this.mCallnack!= null){
			mCallnack.OnUpdateInteface();
		}

	}

}