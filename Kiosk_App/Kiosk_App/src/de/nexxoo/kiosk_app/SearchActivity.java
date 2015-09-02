package de.nexxoo.kiosk_app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.astuetz.PagerSlidingTabStrip;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchActivity extends FragmentActivity {
	private Context mContext;

	private List<Fragment> fragmentList;
	private FragmentAdapter fragmentAdapter;
	private ViewPager pager;

	private List<Manual> mManualList;
	private List<Video> mVideoList;
	private List<Catalog> mCatalogList;

	public static String CONTENTTYPE = "contentTypeId";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_container);
		mContext = this;
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
			//use the query to search your data somehow, async task
			NexxooWebservice.getContent(true, 0, 10, Global.MANUAL_DATABASE_ENTITY_TYPE, new OnJSONResponse() {
				@Override
				public void onReceivedJSONResponse(JSONObject json) {
					try {
						int count = json.getInt("count");
						Log.d(Nexxoo.TAG, "get search result list size is : " + count);
						prepareListData(json);

					} catch (JSONException e) {
						Log.d("KioskError", "Error!" + e.getMessage());
					}
				}

				@Override
				public void onReceivedError(String msg, int code) {
					Log.d("KioskError", "Error!" + msg);
				}
			});
		/**
		 * begin: inflate content frame in container
		 */
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ManualFragment());
		fragmentList.add(new VideoFragment());
		fragmentList.add(new CatalogFragment());

		LayoutInflater inflater = getLayoutInflater();
		FrameLayout container = (FrameLayout) findViewById(R.id.content_frame);
		inflater.inflate(R.layout.activity_main, container);

		pager = (ViewPager) findViewById(R.id.pager);
		fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
				fragmentList,getActionBar());
		pager.setAdapter(fragmentAdapter);
		pager.setOffscreenPageLimit(3);

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setBackgroundColor(getResources().getColor(R.color.RealWhite));
		tabs.setIndicatorColor(getResources().getColor(R.color.Tabs_Indicator_Color));
		Typeface font = Typeface.createFromAsset(getAssets(),"OpenSans-Regular.ttf");
		tabs.setTypeface(font, Typeface.NORMAL);
		tabs.setViewPager(pager);

		tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}
		});

		/**
		 * end: inflate content frame in container
		 */
		}
	}

	private void prepareListData(JSONObject json) {
		try {
			int count = json.getInt("count");
			Manual manual = null;
			Catalog catalog = null;
			Video video = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					int contentTypeId = jsonContentObj.getInt(CONTENTTYPE);
					switch (contentTypeId){
						case 1://catalog
							catalog = new Catalog(jsonContentObj);
							mCatalogList.add(catalog);
							break;
						case 2://manual
							manual = new Manual(jsonContentObj);
							mManualList.add(manual);
							break;
						case 3://video
							video = new Video(jsonContentObj);
							mVideoList.add(video);
							break;
					}
				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

}