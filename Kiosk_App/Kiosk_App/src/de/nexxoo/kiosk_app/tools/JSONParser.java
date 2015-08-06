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

				long id = -1;
				String name = null;
				String description = null;
				String url = null;
				String coverUrlBig = null;
				String coverUrlSmall = null;
				String size = null;
				int category = -1;

				for (int i = 0; i < count; i++) {
					BaseEntityObj = jsonObj.getJSONObject(Integer.toString(i));

					id = BaseEntityObj.getLong("id");
					url = BaseEntityObj.getString("url");
					name = BaseEntityObj.getString("name");
					description = BaseEntityObj.getString("description");
					coverUrlBig = BaseEntityObj.getString("coverUrlBig");
					coverUrlSmall = BaseEntityObj.getString("coverUrlSmall");
					size = BaseEntityObj.getString("size");
					category = BaseEntityObj.getInt("category");

					BaseEntity = new BaseEntity(id, url, name);
					BaseEntity.setDescription(description);
					BaseEntity.setCoverUrlBig(coverUrlBig);
					BaseEntity.setCoverUrlSmall(coverUrlSmall);
					BaseEntity.setSize(size);
					BaseEntity.setCategory(category);
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
