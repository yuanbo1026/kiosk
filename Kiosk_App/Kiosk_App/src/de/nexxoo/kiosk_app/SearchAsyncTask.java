package de.nexxoo.kiosk_app;

import android.content.Context;
import android.os.AsyncTask;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.entity.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchAsyncTask extends AsyncTask<String, Integer, Boolean> {

	private String mQuery;
	private ISearchCallback mCallback;
	private List<List<BaseEntity>> mResultList;

	public SearchAsyncTask(Context ctx, String query, ISearchCallback callback) {
		mCallback = callback;
		mQuery = query;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		if (mQuery == null)
			return false;
		mResultList = setData();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result && mCallback != null && mResultList != null) {
			mCallback.onSearchDone(mResultList);
		}
	}

	private List<List<BaseEntity>> setData() {
		ArrayList<BaseEntity> manuals = new ArrayList<BaseEntity>();
		ArrayList<BaseEntity> videos = new ArrayList<BaseEntity>();
		ArrayList<BaseEntity> catalogs = new ArrayList<BaseEntity>();

		List<List<BaseEntity>> chlidList = new ArrayList<List<BaseEntity>>();

		for (int i = 0; i < 4; i++) {
			Manual manual = new Manual();
			manual.setName("Muanl " + i);
			manuals.add(manual);
		}

		for (int i = 0; i < 4; i++) {
			Video video = new Video();
			video.setmName("Video " + i);
			videos.add(video);
		}

		for (int i = 0; i < 4; i++) {
			Catalog video = new Catalog();
			video.setmName("Catalog " + i);
			catalogs.add(video);
		}

		chlidList.add(manuals);
		chlidList.add(videos);
		chlidList.add(catalogs);

		return chlidList;
	}

}
