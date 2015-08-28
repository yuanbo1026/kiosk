package de.nexxoo.kiosk_app.db;

/**
 * Created by b.yuan on 04.08.2015.
 */
public class Content {
	int contentid;
	//unused
	/*String name;
	String url;
	String description;
	String filename;
	int category;*/
	//unused

	/**
	 * by category:
	 * 1. Manual
	 * 2. Video
	 * 3. Catalog
	 */

	public Content() {
	}

	public Content(int contentid) {
		this.contentid = contentid;
	}

	public int getContentid() {
		return contentid;
	}

	public void setContentid(int contentid) {
		this.contentid = contentid;
	}
}
