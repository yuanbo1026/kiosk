package de.nexxoo.kiosk_app.entity;

import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONObject;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class Catalog  extends BaseEntity {

	public static int CONTENTTYPE = 2;
	public Catalog(JSONObject jsonObj) throws KioskContentError {
		super(jsonObj);
	}
	public Catalog(){
	}
}
