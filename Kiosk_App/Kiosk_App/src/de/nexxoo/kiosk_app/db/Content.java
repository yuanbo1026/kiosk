package de.nexxoo.kiosk_app.db;

import de.nexxoo.kiosk_app.exception.KioskContentError;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by b.yuan on 04.08.2015.
 */
public class Content {
	/**
	 * keep the order of contents
	 */
	private int contentId;
	private String name;
	private String url;
	private String fileName;
	private long size;
	private String  coverImageName;
	private int contentTypeId;
	private int duration;
	private int pages;

	public static String PAGES = "pages";
	public static String CONTENTID = "contentid";
	public static String NAME = "name";
	public static String URL = "url";
	public static String FILENAME = "filename";
	public static String SIZE = "size";
	public static String COVERIMAGENAME = "coverimage";
	public static String CONTENTTYPE = "contenttypeid";
	public static String DURATION = "duration";





	public Content(int id, String name, String fileName) {
		setContentId(id);
		setName(name);
		setFileName(fileName);
	}

	public Content() {}

	public Content(JSONObject jsonObj) throws KioskContentError {
		if (jsonObj != null) {
			try {
				contentId = jsonObj.getInt(CONTENTID);
				name = jsonObj.getString(NAME);
				fileName = jsonObj.getString(FILENAME);
				size = jsonObj.getLong(SIZE);
				coverImageName = jsonObj.getString(NAME) +".jpg";
				contentTypeId = jsonObj.getInt(CONTENTTYPE);
				duration = jsonObj.getInt(DURATION);
				pages = jsonObj.getInt(PAGES);
			} catch (JSONException e) {
				throw new KioskContentError("JSON must not be null!");
			}
		} else {
			throw new KioskContentError("JSON must not be null!");
		}
	}

	public Content(int id) {
		setContentId(contentId);
	}

	public int getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(int contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getCoverImageName() {
		return coverImageName;
	}

	public void setCoverImageName(String coverImageName) {
		this.coverImageName = coverImageName;
	}
}
