package de.nexxoo.kiosk_app.entity;

import android.os.Parcel;
import android.os.Parcelable;
import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by b.yuan on 14.08.2015.
 */
public class Cover implements Parcelable {

	public static final int PICTURETYPE_APP_ICON = 1;
	public static final int PICTURETYPE_MAGAZINE_COVER = 2;
	public static final int PICTURETYPE_VIDEO_COVER = 3;
	public static final int PICTURETYPE_SCREENSHOT_APP = 4;
	public static final int PICTURETYPE_SCREENSHOT_MAGAZINE = 5;
	public static final int PICTURETYPE_SCREENSHOT_VIDEO = 6;
	public static final int PICTURETYPE_FEATURED = 7;

	public static final String PICTURETYPEID = "pictureTypeId";
	//	public static final String PICTUREID = "pictureId"; // ist erst mal raus
	public static final String URL = "url";

	private int mPictureTypeId = -1;
	//	private long mPictureId = -1;
	private String mUrl = null;

	public Cover(JSONObject jsonObj) throws KioskContentError {
		if (jsonObj != null) {
			try {
				mPictureTypeId = jsonObj.getInt(PICTURETYPEID);
//				mPictureId = jsonObj.getLong(PICTUREID); // ist erst mal raus
				mUrl = jsonObj.getString(URL);
//				Log.d(Nexxoo.TAG, "url: "+ mUrl);
			} catch (JSONException e) {
				throw new KioskContentError(e.getMessage());
			}
		} else {
			throw new KioskContentError("JSON must not be null!");
		}
	}

	public int getmPictureTypeId() {
		return mPictureTypeId;
	}

	public String getmUrl() {
		return mUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mUrl);
		dest.writeInt(mPictureTypeId);
	}

	public Cover(Parcel in) {

		mUrl = in.readString();
		mPictureTypeId = in.readInt();
	}

	public static final Parcelable.Creator<Cover> CREATOR = new Parcelable.Creator<Cover>() {
		public Cover createFromParcel(Parcel in) {
			return new Cover(in);
		}

		public Cover[] newArray(int size) {
			return new Cover[size];
		}
	};
}
