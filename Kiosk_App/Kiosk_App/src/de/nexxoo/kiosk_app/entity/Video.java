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

}
