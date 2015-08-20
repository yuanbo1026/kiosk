package de.nexxoo.kiosk_app.entity;

import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONObject;

/**
 * Created by b.yuan on 28.07.2015.
 */
public class Manual extends BaseEntity {
	public static int CONTENTTYPE = 2;
	public Manual(JSONObject jsonObj) throws KioskContentError {
		super(jsonObj);
	}
	public Manual(){
	}
}
