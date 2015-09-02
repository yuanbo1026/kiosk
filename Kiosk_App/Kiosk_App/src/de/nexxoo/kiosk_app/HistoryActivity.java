package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 04.08.2015.
 */
public class HistoryActivity extends Activity {

	private SwipeMenuListView listview;
	private List<BaseEntity> mBaseEntityList = new ArrayList<BaseEntity>();
	private HistoryListAdapter listAdapter;

	private Context mContext;

	private FileStorageHelper mFileStorgeHelper;
	private DatabaseHandler mDatabaseHandler;
	private static final int CONTENT_TYPE_MAUNAL = 2;
	private static final int CONTENT_TYPE_CATALOG = 1;
	private static final int CONTENT_TYPE_VIDEO = 3;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(R.drawable.ic_chevron_left_white_36dp);
		getActionBar().setTitle(Nexxoo.getStyledText(this, "Verlauf"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		mContext = this;


		mFileStorgeHelper = new FileStorageHelper(mContext);
		mDatabaseHandler = new DatabaseHandler(mContext);
		listview = (SwipeMenuListView) findViewById(R.id.history_list);

		getHistoryContetnsFromWebServer();
		initSwipeListView(Global.isNormalScreenSize);
	}

	private Integer[] getContentIdFromDB() {
		/*List<Integer> integerList = mDatabaseHandler.getAllContents();
		Integer[] ids = new Integer[integerList.size()];
        ids = integerList.toArray(ids);*/
		return Nexxoo.getContentIds(mContext);
	}

	private void getHistoryContetnsFromWebServer() {
		Integer[] ids = getContentIdFromDB();
		for (int i : ids) {
//			Log.d(Nexxoo.TAG, "get ids from SharedPreference : " + i);
		}
		//using test static data
		//Integer[] ids = new Integer[]{6,7,4,11,5};

		NexxooWebservice.getContentByIds(true, ids, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					prepareListData(json);
					listAdapter = new HistoryListAdapter(mContext, Global.isNormalScreenSize ? R.layout
							.history_listview_item : R.layout.history_listview_item_big, mBaseEntityList);
					listview.setAdapter(listAdapter);
				} catch (Exception e) {
					Log.d("KioskError", "Error!" + e.getMessage());
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Log.d("KioskError", "Error!" + msg);
			}
		});
	}

	private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem download = new SwipeMenuItem(mContext);
				download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
				download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
				download.setIcon(R.drawable.ic_list_download);
				menu.addMenuItem(download);

				if (menu.getViewType() == CONTENT_TYPE_VIDEO) {
					SwipeMenuItem play = new SwipeMenuItem(mContext);
					play.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
					play.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					play.setIcon(R.drawable.ic_list_play);
					menu.addMenuItem(play);
				} else {
					SwipeMenuItem view = new SwipeMenuItem(mContext);
					view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
					view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					view.setIcon(R.drawable.ic_list_view);
					menu.addMenuItem(view);
				}
			}
		};
		// set creator
		listview.setMenuCreator(creator);

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				int contentTypeId = menu.getViewType();
				switch (index) {
					case 0:
						if (contentTypeId == CONTENT_TYPE_MAUNAL || contentTypeId == CONTENT_TYPE_CATALOG) {
							String filename = mBaseEntityList.get(position).getFileName();
							if (mFileStorgeHelper.isContentDownloaded(filename)) {
								File file = new File(mFileStorgeHelper.getFileAbsolutePath(filename));
								Intent target = new Intent(Intent.ACTION_VIEW);
								target.setDataAndType(Uri.fromFile(file), "application/pdf");
								target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

								Intent i = Intent.createChooser(target, "Open File");
								startActivity(i);
							} else {
								if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
									SwipeMenuLayout menuLayout = (SwipeMenuLayout)
											listview.getChildAt(position);
									menuLayout.smoothCloseMenu();
								}
								DownloadAsyncTask task = new DownloadAsyncTask(mContext,
										mBaseEntityList
												.get(position).getUrl(), mBaseEntityList.get
										(position).getFileName(), Global
										.DOWNLOAD_TASK_TYPE_PDF);
								task.execute();

							}
						} else {
							//click event for video
							DownloadAsyncTask task = new DownloadAsyncTask(mContext,
									mBaseEntityList
											.get(position).getUrl(), mBaseEntityList.get
									(position).getFileName());
							task.execute();
						}

						break;
					case 1:
						if (contentTypeId == CONTENT_TYPE_MAUNAL || contentTypeId == CONTENT_TYPE_CATALOG) {
							// click event for manual and catalog
							String filename1 = mBaseEntityList.get(position).getFileName();
							if (mFileStorgeHelper.isContentDownloaded(filename1)) {
								File file = new File(mFileStorgeHelper.getFileAbsolutePath
										(filename1));
								Intent target = new Intent(Intent.ACTION_VIEW);
								target.setDataAndType(Uri.fromFile(file), "application/pdf");
								target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

								Intent i = Intent.createChooser(target, "Open File");
								startActivity(i);
							} else {
								if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
									SwipeMenuLayout menuLayout = (SwipeMenuLayout)
											listview.getChildAt(position);
									menuLayout.smoothCloseMenu();
								}
								DownloadAsyncTask task1 = new DownloadAsyncTask(mContext,
										mBaseEntityList
												.get(position).getUrl(), mBaseEntityList.get
										(position).getFileName(), Global
										.DOWNLOAD_TASK_TYPE_PDF);
								task1.execute();

							}
						} else {
							//click event for video
							Boolean isVideoDownloaded = mFileStorgeHelper.isContentDownloaded(mBaseEntityList
									.get(position).getFileName());
							String url = mBaseEntityList.get(position).getUrl();
							url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
							Intent i = new Intent(mContext, VideoActivity.class);
							i.putExtra(mContext.getString(R.string
									.video_activity_intent_url_extra), url);
							String name = mBaseEntityList.get(position).getFileName();
							i.putExtra("filename", name);
							i.putExtra("isVideoDownloaded", isVideoDownloaded);
							mContext.startActivity(i);
						}

						break;
				}
				return false;
			}
		});

		// set SwipeListener
		listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listview.smoothOpenMenu(position);
			}
		});
	}

	private void prepareListData(JSONObject json) {
		try {
			int count = json.getInt("count");
			List<BaseEntity> tmpList = new ArrayList<BaseEntity>();
			List<Integer> ids = Nexxoo.getContentIdList(mContext);
			BaseEntity mBaseEntity = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					mBaseEntity = new BaseEntity(jsonContentObj);
//					mBaseEntityList.add(mBaseEntity);
					tmpList.add(mBaseEntity);


				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}
			for (int id : ids) {
				for (BaseEntity base : tmpList) {
					if (base != null && base.getContentId() == id)
						mBaseEntityList.add(base);
					Log.d(Nexxoo.TAG, "History item lite content ids :" + base.getContentId());
				}
			}
			}catch(JSONException e){
				Log.d(Nexxoo.TAG, e.getMessage());
			}
		}

		@Override
		public boolean onOptionsItemSelected (MenuItem menuItem){
			switch (menuItem.getItemId()) {
				case android.R.id.home:
					finish();
			}
			return (super.onOptionsItemSelected(menuItem));
		}

	}