package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ExpandableListView;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchActivity extends Activity implements ISearchCallback {
	private ExpandableListView listview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_fragment);
		/**
		 * using background task to solve the efficient problem
		 */
/*		Intent intent = getIntent();
		listview = (ExpandableListView) findViewById(R.id.search_result_listview);
		handleIntent(intent);*/

		/**
		 * basic expandable listview without background task
		 */
/*		listview = (ExpandableListView) findViewById(R.id.search_result_listview);
		List<String> groupList = new ArrayList<String>();
		groupList.add("Manual");
		groupList.add("Video");
		groupList.add("Catalog");
		SearchListAdapter adapter = new SearchListAdapter(this, groupList, setData());
		listview.setAdapter(adapter);*/
		/**
		 * below is the easy working expandable listview and DB test
		 */
		Intent intent = getIntent();
		List<String> groupList = new ArrayList<String>();
		groupList.add("Manual");
		groupList.add("Video");
		groupList.add("Catalog");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			ExpandableListView myExpandableListView = (ExpandableListView) findViewById(R.id.search_result_listview);
			myExpandableListView.setAdapter(new SearchListAdapter(this, groupList, setData()));

			DatabaseHandler db = new DatabaseHandler(this);

			/*if(db.getContent("g")!= null){
				Log.d("search context","we get the context");
			}else{
				Log.d("search context","we don't get the context");
			}*/
			/*db.addContent(new Content("d", "d", "d", "d", 1));
			db.addContent(new Content("e", "e", "e", "e", 1));
			db.addContent(new Content("f", "f", "f", "f", 1));

			Log.d("Reading: ", "Reading all contacts..");
			List<Content> contents = db.getAllContents();

			for (Content cn : contents) {
				String log = "Name: " + cn.getName() + " ,Description: " + cn.getDescription();
				// Writing Contacts to log
				Log.d("Name: ", log);
			}*/
		}
		/**
		 * above is the easy working expandable listview and DB test
		 */

	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		super.onNewIntent(intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			//use the query to search your data somehow
			SearchAsyncTask task = new SearchAsyncTask(this, query, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {

			}
		}

		/**
		 * get group list from callback(onSearchDone) method
		 */
		SearchResultGroupList srg = intent.getParcelableExtra(getString(R.string.kiosk_search_bundle_group));
		SearchResultChildList src = intent.getParcelableExtra(getString(R.string.kiosk_search_bundle_child));

		if (srg != null && srg.getGroupList() != null && src != null && src.getChildList() != null) {
			listview.setAdapter(new SearchListAdapter(this, srg.getGroupList(), src.getChildList()));
		}


	}

	@Override
	public void onSearchDone(List<List<BaseEntity>> entityList) {
		List<String> groupList = new ArrayList<String>();
		groupList.add("Manual");
		groupList.add("Video");
		groupList.add("Catalog");

//		listview.setAdapter(new SearchListAdapter(this,groupList, entityList));
		Intent i = new Intent(this, SearchActivity.class);
		i.putExtra(getString(R.string.kiosk_search_bundle_child), new SearchResultChildList(entityList));
		i.putExtra(getString(R.string.kiosk_search_bundle_group), new SearchResultGroupList(groupList));

		handleIntent(i);
	}

	private List<List<BaseEntity>> setData() {
		List<BaseEntity> manuals = new ArrayList<BaseEntity>();
		List<BaseEntity> videos = new ArrayList<BaseEntity>();
		List<BaseEntity> catalogs = new ArrayList<BaseEntity>();

		List<List<BaseEntity>> chlidList = new ArrayList<List<BaseEntity>>();

		for (int i = 0; i < 4; i++) {
			Manual manual = new Manual();
			manual.setName("Manual " + i);
			manuals.add(manual);
		}

		for (int i = 0; i < 4; i++) {
			Video video = new Video();
			video.setName("Video " + i);
			videos.add(video);
		}

		for (int i = 0; i < 4; i++) {
			Catalog video = new Catalog();
			video.setName("Catalog " + i);
			catalogs.add(video);
		}

		chlidList.add(manuals);
		chlidList.add(videos);
		chlidList.add(catalogs);

		return chlidList;
	}

}