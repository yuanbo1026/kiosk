package de.nexxoo.kiosk_app.db;

/**
 * Created by b.yuan on 04.08.2015.
 */
public class Content {
	String name;
	String url;
	String description;
	String filename;
	int category;

	/**
	 * by category:
	 * 1. Manual
	 * 2. Video
	 * 3. Catalog
	 */

	public Content() {
	}

	public Content(String name, String url, String description, String filename, int category) {
		this.name = name;
		this.url = url;
		this.description = description;
		this.filename = filename;
		this.category = category;
	}

	public Content(String name, String url, String filename, int category) {
		this.name = name;
		this.url = url;
		this.filename = filename;
		this.category = category;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
}
