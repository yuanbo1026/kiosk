package de.nexxoo.kiosk_app.entity;

import de.nexxoo.kiosk_app.db.Content;
import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class Catalog extends BaseEntity {
	public static String PAGES = "pages";
	private int pages;

	public Catalog(JSONObject jsonObj) throws KioskContentError {
		super(jsonObj);
		try {
			pages = jsonObj.getInt(PAGES);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Catalog(Content content){
		pages = content.getPages();
	}
	public Catalog() {
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
}
