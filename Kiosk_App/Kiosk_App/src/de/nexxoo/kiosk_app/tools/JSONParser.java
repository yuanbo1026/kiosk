package de.nexxoo.kiosk_app.tools;

import android.os.Parcel;
import android.os.Parcelable;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

	public static List<BaseEntity> parseJsonToBaseEntityList(String json) {
		List<BaseEntity> sList = new ArrayList<BaseEntity>();
		if (json != null) {
			try {
				JSONObject jsonObj = new JSONObject(json);
				int count = jsonObj.getInt("count");
				JSONObject BaseEntityObj;
				BaseEntity BaseEntity = null;

				int id = -1;
				String name = null;
				String description = null;
				String url = null;
				String fileName = null;
				long size;

				for (int i = 0; i < count; i++) {
					BaseEntityObj = jsonObj.getJSONObject(Integer.toString(i));

					id = BaseEntityObj.getInt("contentid");
					url = BaseEntityObj.getString("url");
					name = BaseEntityObj.getString("name");
					fileName = BaseEntityObj.getString("filename");
					description = BaseEntityObj.getString("descriptionText");
					size = BaseEntityObj.getLong("size");

					BaseEntity = new BaseEntity(id, name,fileName);
					BaseEntity.setDescription(description);
					BaseEntity.setFileName(fileName);
					BaseEntity.setSize(size);
					sList.add(BaseEntity);
				}
			} catch (JSONException e) {
				//Log.e("Nexxoo","error:"+e.getLocalizedMessage());
			}
		}
		return sList;

	}

	public static List<String> parseJsonToBaseEntityListRandom(String json) {
		List<String> sList = new ArrayList<String>();
		if (json != null) {
			try {
				JSONObject jsonObj = new JSONObject(json);
				int count = jsonObj.getInt("count");

				for (int i = 0; i < count; i++) {

					sList.add(jsonObj.getString(Integer.toString(i)));
				}
			} catch (JSONException e) {
				//Log.e("Nexxoo","error:"+e.getLocalizedMessage());
			}
		}
		return sList;

	}

	public static final Parcelable.Creator<BaseEntity> CREATOR = new Parcelable
			.Creator<BaseEntity>() {
		public BaseEntity createFromParcel(Parcel in) {
			return new BaseEntity(in);
		}
		public BaseEntity[] newArray(int size) {
			return new BaseEntity[size];
		}
	};

}
