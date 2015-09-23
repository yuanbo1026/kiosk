package de.nexxoo.kiosk_app;

import android.app.ProgressDialog;
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
import de.nexxoo.kiosk_app.entity.BaseEntity;
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
	private DownloadCallback mCallback;
	private String mImageUrl;
	private String mImageName;

	public DownloadAsyncTask(Context context, String url, String filename) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		}
	}

	public DownloadAsyncTask(Context context, BaseEntity base) {
		this.mContext = context;
		this.mUrl = base.getUrl().replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = base.getFileName();
		this.entityType = base.getContentTypeId();
		this.mImageUrl = base.getmPictureList().get(0).getmUrl();
		this.mImageName = base.getName() + ".jpg";
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		}
	}


	public DownloadAsyncTask(Context context, String url, String filename, int entityType) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		}
	}

	public DownloadAsyncTask(Context context, String pdfUrl, String pdfName,String imageUrl,String
			imageName, int
			entityType) {
		this.mContext = context;
		this.mUrl = pdfUrl.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = pdfName;
		this.mImageUrl = imageUrl;
		this.mImageName = imageName;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		}
	}

	public DownloadAsyncTask(Context context, String url, String filename, int entityType, DownloadCallback callback) {
		this.mContext = context;
		this.mUrl = url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
		this.filename = filename;
		this.entityType = entityType;
		if (BASICAUTH != null) {
			basicAuthBase64 = Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		}
		this.mCallback = callback;
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
		File downloadFolder = new File(fileStorageHelper.getDownloadFolder());
		if (!downloadFolder.exists())
			downloadFolder.mkdirs();

		File imageFolder = new File(fileStorageHelper.getImageFolder());
		if (!imageFolder.exists())
			imageFolder.mkdirs();

		if (fileStorageHelper.isContentDownloaded(filename)) {
			Toast.makeText(mContext, mContext.getString(R.string.download_file_exist), Toast
					.LENGTH_LONG).show();
			onPostExecute(filename);
		}
	}

	protected String doInBackground(Void... urls) {
		InputStream input = null;
		OutputStream output = null;
		FileStorageHelper helper = new FileStorageHelper(mContext);
		try {
			URL url = new URL(mUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", basicAuthBase64);
			connection.setRequestProperty("Accept-Encoding", "identity");
			if (entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_PDF)
				connection.setRequestProperty("User-Agent", "nexxoo");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(Nexxoo.TAG, "HttpURLConnection: " + connection.getResponseCode());
				Log.e(Nexxoo.TAG, connection.getErrorStream().toString());
			}

			int fileLength = connection.getContentLength();
			input = connection.getInputStream();

			File folder = new File(helper.getDownloadFolder());
			if (!folder.exists())
				folder.mkdirs();

			output = new FileOutputStream(fileStorageHelper.getDownloadAbsolutePath(filename));

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
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Nexxoo.TAG, "Exception: " + e.getMessage());
		}
		if (mImageUrl != null){
			try {
				URL url = new URL(mImageUrl);
				HttpURLConnection connections = (HttpURLConnection) url.openConnection();
//				Log.e(Nexxoo.TAG, "image url:" + mImageUrl);
				connections.setDoInput(true);
				connections.setDoOutput(true);
				connections.connect();
				if (connections.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Log.e(Nexxoo.TAG, "HttpURLConnection: " + connections.getResponseCode());
					Log.e(Nexxoo.TAG, connections.getErrorStream().toString());
					return "";
				}

				int fileLength = connections.getContentLength();
				input = connections.getInputStream();

				File folder = new File(helper.getImageFolder());
				if (!folder.exists())
					folder.mkdirs();

				output = new FileOutputStream(fileStorageHelper.getImageAbsolutePath(mImageName));

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
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(Nexxoo.TAG, "Exception: " + e.getMessage());
			}
		}


		return fileStorageHelper.getDownloadAbsolutePath(filename);

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
		 * display PDF file
		 */
		if (entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_PDF) {
			File file = new File(filename);
			Intent target = new Intent(Intent.ACTION_VIEW);
			target.setDataAndType(Uri.fromFile(file), "application/pdf");
			target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			Intent intent = Intent.createChooser(target, mContext.getString(R.string
					.open_pdf_file));
			mContext.startActivity(intent);
		} else if (entityType != -1 && entityType == Global.DOWNLOAD_TASK_TYPE_VIDEO) {
		}

		if (this.mCallback != null) {
			mCallback.OnUpdateInteface();
		}

	}

}