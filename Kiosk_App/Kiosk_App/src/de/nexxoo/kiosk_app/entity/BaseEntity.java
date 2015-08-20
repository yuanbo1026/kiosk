package de.nexxoo.kiosk_app.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import de.nexxoo.kiosk_app.exception.KioskContentError;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class BaseEntity implements Parcelable {
	/**
	 * keep the order of contents
	 */
	private int contentId;
	private String name;
	private String description;
	private String url;
	private String fileName;
	private long size;
	private List<Cover> mPictureList = new ArrayList<Cover>();
	private List<Bitmap> mBitmap = new ArrayList<Bitmap>();

	public static String CONTENTID = "contentId";
	public static String NAME = "name";
	public static String DESCRIPTION = "description";
	public static String URL = "url";
	public static String FILENAME = "filename";
	public static String SIZE = "size";

	public static String PICTURES = "pictures";
	private static final String COUNT = "count";
	public static String CONTENTTYPE = "contentTypeId";



	public long getSize() {
		return size;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Cover> getmPictureList() {
		return mPictureList;
	}

	public void setmPictureList(List<Cover> mPictureList) {
		this.mPictureList = mPictureList;
	}

	public List<Bitmap> getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(List<Bitmap> mBitmap) {
		this.mBitmap = mBitmap;
	}

	public BaseEntity(int id, String name, String fileName) {
		setContentId(id);
		setName(name);
		setFileName(fileName);
	}

	public BaseEntity() {}

	public BaseEntity(JSONObject jsonObj) throws KioskContentError {
		if (jsonObj != null) {
			try {
				contentId = jsonObj.getInt(CONTENTID);
				name = jsonObj.getString(NAME);
				description = jsonObj.getString(DESCRIPTION);
				url = jsonObj.getString(URL);
				fileName = jsonObj.getString(FILENAME);

				JSONObject jsonObjPics = jsonObj.getJSONObject(PICTURES);
				if (jsonObjPics != null) {
					JSONObject jsonObjPic = null;
					int count = jsonObjPics.getInt(COUNT);
					for (int i = 0; i < count; i++) {
						if (jsonObjPics.has(("picture" + Integer.toString(i)))) {
							jsonObjPic = jsonObjPics.getJSONObject("picture" + Integer.toString(i));
							Cover pic = new Cover(jsonObjPic);
							mPictureList.add(pic);
//							new GrabBitmap().execute(url);
						}
					}
				}
				size = jsonObj.getLong(SIZE);
				fileName = jsonObj.getString(FILENAME);

			} catch (JSONException e) {
				throw new KioskContentError("JSON must not be null!");
			}
		} else {
			throw new KioskContentError("JSON must not be null!");
		}
	}

	private class GrabBitmap extends AsyncTask<String, Integer, Bitmap> {

		protected void onPreExecute() {
		}

		protected Bitmap doInBackground(String... urls) {
			Log.d(Nexxoo.TAG, "Bitmap URL : "+urls[0]);
			Bitmap bitmap = null;
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) new URL(urls[0])
						.openConnection();
				connection.setRequestProperty("User-Agent", "nexxoo");
				connection.connect();
				InputStream input = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;

		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onCancelled() {
		}
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null){
//				item.manualCover.setImageBitmap(bitmap);
				mBitmap.add(bitmap);
			}
			else{
				Log.d(Nexxoo.TAG, "cannot download Cover Image");
			}
		}
	}

	public BaseEntity(int id) {
		setContentId(contentId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(contentId);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(url);
		dest.writeString(fileName);
		dest.writeLong(size);
		dest.writeList(mPictureList);
	}

	public BaseEntity(Parcel in) {

		long[] longData = new long[1];
		String[] stringData = new String[4];
		int[] intData = new int[1];

		in.readIntArray(intData);
		contentId = intData[0];

		in.readStringArray(stringData);
		name = stringData[0];
		description = stringData[1];
		url = stringData[2];
		fileName = stringData[3];

		in.readLongArray(longData);
		size = longData[0];

		in.readList(mPictureList, Cover.class.getClassLoader());
	}
}
