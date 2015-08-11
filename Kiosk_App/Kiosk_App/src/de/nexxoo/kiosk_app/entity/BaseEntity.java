package de.nexxoo.kiosk_app.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class BaseEntity implements Parcelable {
	private long id;
	/**
	 * keep the order of contents
	 */
	private String name;
	private String description;
	private String url;
	private String fileName;
	private String coverUrlBig;
	private String coverUrlSmall;
	private String size;
	private int category;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getCoverUrlBig() {
		return coverUrlBig;
	}

	public void setCoverUrlBig(String coverUrlBig) {
		this.coverUrlBig = coverUrlBig;
	}

	public String getCoverUrlSmall() {
		return coverUrlSmall;
	}

	public void setCoverUrlSmall(String coverUrlSmall) {
		this.coverUrlSmall = coverUrlSmall;
	}

	public BaseEntity(long id, String name, String url,String fileName){
		setId(id);
		setName(name);
		setUrl(url);
		setFileName(fileName);
	}

	public BaseEntity(){

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(url);
		dest.writeString(fileName);
		dest.writeString(coverUrlBig);
		dest.writeString(coverUrlSmall);
		dest.writeString(size);
		dest.writeInt(category);
	}

	public BaseEntity(Parcel in){
		name = in.readString();
		description = in.readString();
		url = in.readString();
		fileName = in.readString();
		coverUrlBig = in.readString();
		coverUrlSmall = in.readString();
		size = in.readString();
		category = in.readInt();
	}
}
