package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.nexxoo.kiosk_app.db.Content;
import de.nexxoo.kiosk_app.db.ContentDBHelper;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
	private static final int CONTENT_TYPE_MAUNAL = 2;
	private static final int CONTENT_TYPE_CATALOG = 1;
	private static final int CONTENT_TYPE_VIDEO = 3;

	private boolean isVideoDownloaded;
	public static String CONTENTTYPE = "contentTypeId";
	private boolean isWifiOn;
	private boolean fromMenu;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(R.drawable.ic_arrow_back);
		getActionBar().setTitle(Nexxoo.getStyledText(this, "Verlauf"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		mContext = this;
		Intent i = getIntent();
		isWifiOn = i.getBooleanExtra(getString(R.string.wifi_status), true);
		fromMenu = i.getBooleanExtra(getString(R.string.from_menu_click),false);

		mFileStorgeHelper = new FileStorageHelper(mContext);
		listview = (SwipeMenuListView) findViewById(R.id.history_list);
		TextView emptyView = new TextView(mContext);
		emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		emptyView.setText(Nexxoo.getStyledText(mContext, mContext.getString(R.string
				.search_no_result)));
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		emptyView.setVisibility(View.GONE);
		((ViewGroup) listview.getParent()).addView(emptyView);
		listview.setEmptyView(emptyView);

		if (isWifiOn)
			getHistoryContentsFromWebServer();
		else
			getHistoryContentsFromDB();
		initSwipeListView(Global.isNormalScreenSize);
	}

	private Integer[] getContentIdFromSharedPreferences() {
		return Nexxoo.getContentIds(mContext);
	}

	private void getHistoryContentsFromWebServer() {
		Integer[] ids = getContentIdFromSharedPreferences();

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

	private void getHistoryContentsFromDB() {
		ContentDBHelper db = new ContentDBHelper(this);
		List<Content> list = db.getAllContacts();
		convertContentToBaseEntity(list);
		listAdapter = new HistoryListAdapter(mContext, Global.isNormalScreenSize ? R.layout
				.history_listview_item : R.layout.history_listview_item_big, mBaseEntityList, isWifiOn);
		listview.setAdapter(listAdapter);
	}

	private void convertContentToBaseEntity(List<Content> list) {
		mBaseEntityList.clear();
		Collections.reverse(list);
		for (Content content : list) {
			int contentTypeId = content.getContentTypeId();
			switch (contentTypeId) {
				case 1:
					Catalog catalog = new Catalog();
					catalog.setContentId(content.getContentId());
					catalog.setName(content.getName());
					catalog.setUrl(content.getUrl());
					catalog.setFileName(content.getFileName());
					catalog.setSize(content.getSize());
					catalog.setContentTypeId(1);
					catalog.setPages(content.getPages());
					mBaseEntityList.add(catalog);
					break;
				case 2:
					Manual manual = new Manual();
					manual.setContentId(content.getContentId());
					manual.setName(content.getName());
					manual.setUrl(content.getUrl());
					manual.setFileName(content.getFileName());
					manual.setSize(content.getSize());
					manual.setContentTypeId(2);
					manual.setPages(content.getPages());
					mBaseEntityList.add(manual);
					break;
				case 3:
					Video video = new Video();
					video.setContentId(content.getContentId());
					video.setName(content.getName());
					video.setUrl(content.getUrl());
					video.setFileName(content.getFileName());
					video.setSize(content.getSize());
					video.setContentTypeId(3);
					video.setDuration(content.getDuration());
					mBaseEntityList.add(video);
					break;
			}

		}
	}

	private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				if (menu.getViewType() == CONTENT_TYPE_VIDEO) {
					SwipeMenuItem download = new SwipeMenuItem(mContext);
					download.setId(30000);
					download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
					download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					download.setIcon(menu.getViewType() > 0?R.drawable.ic_list_trash:R
							.drawable.ic_list_download);
					download.setIsVisiable(true);
					menu.addMenuItem(download);
				}else{//PDF
					SwipeMenuItem download = new SwipeMenuItem(mContext);
					download.setId(30000);
					download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
					download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					download.setIcon(R.drawable.ic_list_trash);
					download.setIsVisiable(menu.getViewType() > 0);
					menu.addMenuItem(download);
				}

				if (menu.getViewType() == CONTENT_TYPE_VIDEO) {
					SwipeMenuItem play = new SwipeMenuItem(mContext);
					play.setId(40000);
					play.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
					play.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					play.setIcon(R.drawable.ic_list_play);
					play.setIsVisiable(true);
					menu.addMenuItem(play);
				} else {
					SwipeMenuItem view = new SwipeMenuItem(mContext);
					view.setId(40000);
					view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
					view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
					view.setIcon(R.drawable.ic_list_view);
					view.setIsVisiable(true);
					menu.addMenuItem(view);
				}
			}
		};
		// set creator
		listview.setMenuCreator(creator);

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenuView parent,
										   SwipeMenu menu, int index) {
				int contentTypeId = menu.getViewType();

				if (contentTypeId == 3) {//video
					switch (index) {
						case 50000:
							if (mFileStorgeHelper.isContentDownloaded(mBaseEntityList.get(position)
									.getFileName())) {//downloaded
								File video = new File(mFileStorgeHelper.getDownloadAbsolutePath
										(mBaseEntityList
												.get(position).getFileName()));
								if(video.exists())
								video.delete();
								/**
								 * delete content from DB
								 */
								ContentDBHelper db = new ContentDBHelper(mContext);
								db.deleteContent(mBaseEntityList.get(position).getContentId());
								Log.e(Nexxoo.TAG, "delete content from DB :" + db.getContentsCount());

								LinearLayout imageLayout = (LinearLayout) parent
										.findViewById(new
												Integer(50000));
								ImageView image = (ImageView) imageLayout.getChildAt(0);
								image.setImageResource(R.drawable.ic_list_download);
							} else {// not downloaded
								/**
								 * add download content to local DB
								 */
								//should add wifi status check
								Video video = (Video)mBaseEntityList.get(position);
								ContentDBHelper db = new ContentDBHelper(mContext);
								db.addContact(video);

								DownloadAsyncTask task = new DownloadAsyncTask(mContext,
										mBaseEntityList
												.get(position).getUrl(), mBaseEntityList.get
										(position).getFileName());
								task.execute();
								LinearLayout imageLayout = (LinearLayout) parent
										.findViewById(new
												Integer(50000));
								ImageView image = (ImageView) imageLayout.getChildAt(0);
								image.setImageResource(R.drawable.ic_list_trash);
							}
							break;
						case 50001://play button
							isVideoDownloaded = mFileStorgeHelper.isContentDownloaded(mBaseEntityList
									.get(position).getFileName());
							String url1 = mBaseEntityList.get(position).getUrl();

							Intent i1 = new Intent(mContext, VideoActivity.class);
							if (url1 != null) {
								url1.replace("www", "nexxoo:wenexxoo4kiosk!@www");
								i1.putExtra(mContext.getString(R.string
										.video_activity_intent_url_extra), url1);
							}
							String name1 = mBaseEntityList.get(position).getFileName();
							i1.putExtra("filename", name1);
							i1.putExtra("isVideoDownloaded", isVideoDownloaded);
							mContext.startActivity(i1);
							break;
					}
				} else {//PDF
					if (mFileStorgeHelper.isContentDownloaded(mBaseEntityList.get(position)
							.getFileName())) {//two buttons
						switch (index) {
							case 50000:
								File manual = new File(mFileStorgeHelper.getDownloadAbsolutePath
										(mBaseEntityList
												.get(position).getFileName()));
								manual.delete();
								/**
								 * delete content from DB
								 */
								ContentDBHelper db = new ContentDBHelper(mContext);
								db.deleteContent(mBaseEntityList.get(position).getContentId());
								Log.e(Nexxoo.TAG, "delete content from DB :" + db.getContentsCount());

								LinearLayout image = (LinearLayout) parent.findViewById(new
										Integer(50000));
								image.setVisibility(View.GONE);
								//invisible trash icon
								break;
							case 50001:
								BaseEntity base = mBaseEntityList.get(position);
								String filename = base.getFileName();
								if (mFileStorgeHelper.isContentDownloaded(filename)) {
									listview.closeAllMenu();
									File file = new File(mFileStorgeHelper.getDownloadAbsolutePath(filename));
									Intent target = new Intent(Intent.ACTION_VIEW);
									target.setDataAndType(Uri.fromFile(file), "application/pdf");
									target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									Intent i = Intent.createChooser(target, mContext
											.getString(R.string.open_pdf_file));
									startActivity(i);
								} else {
									listview.closeAllMenu();
									DownloadAsyncTask task1 = new DownloadAsyncTask(mContext,
											base.getUrl(),
											base.getFileName());
									task1.execute();
								}
								break;
						}
					} //on history list all PDF are already be downloaded.
				}
				return false;
			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listview.smoothOpenMenu(position);
			}
		});
		listview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				listview.closeAllMenu();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	private void prepareListData(JSONObject json) {
		mBaseEntityList.clear();
		try {
			int count = json.getInt("count");
			Manual manual = null;
			Catalog catalog = null;
			Video video = null;
			List<BaseEntity> tmpList = new ArrayList<BaseEntity>();
			List<Integer> ids = Nexxoo.getContentIdList(mContext);
			BaseEntity mBaseEntity = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					int contentTypeId = jsonContentObj.getInt(CONTENTTYPE);

					switch (contentTypeId) {
						case 1://catalog
							catalog = new Catalog(jsonContentObj);
							tmpList.add(catalog);
							break;
						case 2://manual
							manual = new Manual(jsonContentObj);
							tmpList.add(manual);
							break;
						case 3://video
							video = new Video(jsonContentObj);
							tmpList.add(video);
							break;
					}

//					mBaseEntity = new BaseEntity(jsonContentObj);
//					tmpList.add(mBaseEntity);
				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}
			for (int id : ids) {
				for (BaseEntity base : tmpList) {
					if (base != null && base.getContentId() == id)
						mBaseEntityList.add(base);
				}
			}

			/*if (mBaseEntityList.size() > 0) {
				Collections.sort(mBaseEntityList, new Comparator<BaseEntity>() {
					@Override
					public int compare(final BaseEntity object1, final BaseEntity object2) {
						return object1.getName().compareTo(object2.getName());
					}
				});
			}*/
		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				if (!Misc.isOnline(this)) {
					new AlertDialog.Builder(this)
							.setMessage(this.getResources().getString(R.string.no_wifi_message))
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
				}else{
					if(fromMenu)
						finish();
					else{
						Intent i = new Intent(HistoryActivity.this, MainActivity.class);
						i.putExtra(getString(R.string.wifi_status), false);
						startActivity(i);
					}

				}
		}
		return (super.onOptionsItemSelected(menuItem));
	}

}