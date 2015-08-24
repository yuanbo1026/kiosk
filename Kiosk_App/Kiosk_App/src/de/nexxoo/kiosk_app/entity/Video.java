package de.nexxoo.kiosk_app.entity;

import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONObject;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class Video extends BaseEntity {

	public Video(JSONObject jsonObj) throws KioskContentError {
		super(jsonObj);
	}
	public Video(){
	}

	private long mId;
	private String mManualUrl;
	private String mName;
	private String mSize;

	public String getmSize() {
		return mSize;
	}

	public void setmSize(String mSize) {
		this.mSize = mSize;
	}

	public long getmId() {
		return mId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}

	public String getmManualUrl() {
		return mManualUrl;
	}

	public void setmManualUrl(String mManualUrl) {
		this.mManualUrl = mManualUrl;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}
}
