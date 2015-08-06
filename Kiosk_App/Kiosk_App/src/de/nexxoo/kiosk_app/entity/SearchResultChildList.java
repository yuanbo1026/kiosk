package de.nexxoo.kiosk_app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchResultChildList implements Parcelable {
	private List<List<BaseEntity>> mChlidList;

	public SearchResultChildList(List<List<BaseEntity>> mChlidList){
		setChildList(mChlidList);
	}

	public List<List<BaseEntity>> getChildList() {
		return mChlidList;
	}

	public void setChildList(List<List<BaseEntity>> childList) {
		this.mChlidList = childList;
	}

	// Parcelling part
	public SearchResultChildList(Parcel in){
		this.mChlidList = new ArrayList<List<BaseEntity>>();
		in.readList(this.mChlidList, ArrayList.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.getChildList());
	}
	public static final Parcelable.Creator<SearchResultChildList> CREATOR = new Parcelable.Creator<SearchResultChildList>() {
		public SearchResultChildList createFromParcel(Parcel in) {
			return new SearchResultChildList(in);
		}

		public SearchResultChildList[] newArray(int size) {
			return new SearchResultChildList[size];
		}
	};
}
