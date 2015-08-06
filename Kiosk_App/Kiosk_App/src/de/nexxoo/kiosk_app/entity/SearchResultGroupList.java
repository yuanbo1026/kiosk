package de.nexxoo.kiosk_app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchResultGroupList implements Parcelable {
	private List<String> mGroupList;

	public SearchResultGroupList(List<String> groupnList){
		setGroupList(groupnList);
	}

	public List<String> getGroupList() {
		return mGroupList;
	}

	public void setGroupList(List<String> stationList) {
		this.mGroupList = stationList;
	}

	// Parcelling part
	public SearchResultGroupList(Parcel in){
		this.mGroupList = new ArrayList<String>();
		in.readList(this.mGroupList, String.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.getGroupList());
	}
	public static final Parcelable.Creator<SearchResultGroupList> CREATOR = new Parcelable.Creator<SearchResultGroupList>() {
		public SearchResultGroupList createFromParcel(Parcel in) {
			return new SearchResultGroupList(in);
		}

		public SearchResultGroupList[] newArray(int size) {
			return new SearchResultGroupList[size];
		}
	};
}
