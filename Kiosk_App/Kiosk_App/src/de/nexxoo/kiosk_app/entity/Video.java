package de.nexxoo.kiosk_app.entity;

import de.nexxoo.kiosk_app.db.Content;
import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class Video extends BaseEntity {
	public static String DURATION = "videoDuration";
	private int duration;

	public Video(JSONObject jsonObj) throws KioskContentError {
		super(jsonObj);
		try {
			duration = jsonObj.getInt(DURATION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Video(Content content){
		duration = content.getDuration();
	}
	public Video() {
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
