package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.nexxoo.kiosk_app.db.ContentDBHelper;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.09.2015.
 */
public class SearchResultAllFragment extends Fragment {
	private SwipeMenuListView listview;
	private List<BaseEntity> mBaseEntityList = new ArrayList<BaseEntity>();
	private SearchResultAllListAdapter listAdapter;

	private Context mContext;

	private FileStorageHelper mFileStorgeHelper;
	private static final int CONTENT_TYPE_MAUNAL_DOWNLOADED = 21;
	private static final int CONTENT_TYPE_MAUNAL_NOT_DOWNLOADED = 20;
	private static final int CONTENT_TYPE_CATALOG_DOWNLOADED = 11;
	private static final int CONTENT_TYPE_CATALOG_NOT_DOWNLOADED = 10;
	private static final int CONTENT_TYPE_VIDEO_DOWNLOADED = 31;
	private static final int CONTENT_TYPE_VIDEO_NOT_DOWNLOADED = 30;
	private boolean isVideoDownloaded;

	public static SearchResultAllFragment newInstance(Context context,
													  List<BaseEntity> entityList) {
		SearchResultAllFragment f = new SearchResultAllFragment();
		Bundle args = new Bundle();
		args.putSerializable(context.getString(R.string.search_result_all_list), (Serializable) entityList);
		f.setArguments(args);
		return f;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = this.getActivity();
		mFileStorgeHelper = new FileStorageHelper(mContext);
		mBaseEntityList = (List<BaseEntity>) getArguments().getSerializable(mContext
				.getString(R.string
						.search_result_all_list));

		View rootView = inflater.inflate(R.layout.history, container, false);

		mFileStorgeHelper = new FileStorageHelper(mContext);
		listview = (SwipeMenuListView) rootView.findViewById(R.id.history_list);
		TextView emptyView = new TextView(mContext);
		emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		emptyView.setText(Nexxoo.getStyledText(mContext, mContext.getString(R.string
				.search_no_result)));
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		emptyView.setVisibility(View.GONE);
		((ViewGroup) listview.getParent()).addView(emptyView);
		listview.setEmptyView(emptyView);
		getHistoryContetnsFromWebServer();
		initSwipeListView(Global.isNormalScreenSize);


		return rootView;
	}

	private void getHistoryContetnsFromWebServer() {
		listAdapter = new SearchResultAllListAdapter(mContext, Global.isNormalScreenSize ? R.layout
				.history_listview_item : R.layout.history_listview_item_big, mBaseEntityList);
		listview.setAdapter(listAdapter);
	}

	private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
					case CONTENT_TYPE_VIDEO_DOWNLOADED:
						SwipeMenuItem download = new SwipeMenuItem(mContext);
						download.setId(30000);
						download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						download.setIcon(R.drawable.ic_list_trash);
						download.setIsVisiable(true);
						menu.addMenuItem(download);

						SwipeMenuItem play = new SwipeMenuItem(mContext);
						play.setId(40000);
						play.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						play.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						play.setIcon(R.drawable.ic_list_play);
						play.setIsVisiable(true);
						menu.addMenuItem(play);
						break;
					case CONTENT_TYPE_VIDEO_NOT_DOWNLOADED:
						SwipeMenuItem video_not_download = new SwipeMenuItem(mContext);
						video_not_download.setId(30000);
						video_not_download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						video_not_download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						video_not_download.setIcon(R.drawable.ic_list_download);
						video_not_download.setIsVisiable(true);
						menu.addMenuItem(video_not_download);

						SwipeMenuItem video_play = new SwipeMenuItem(mContext);
						video_play.setId(40000);
						video_play.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						video_play.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						video_play.setIcon(R.drawable.ic_list_play);
						video_play.setIsVisiable(true);
						menu.addMenuItem(video_play);
						break;
					case CONTENT_TYPE_CATALOG_DOWNLOADED:
						SwipeMenuItem catalog_downloaded = new SwipeMenuItem(mContext);
						catalog_downloaded.setId(30000);
						catalog_downloaded.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						catalog_downloaded.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						catalog_downloaded.setIcon(R.drawable.ic_list_trash);
						catalog_downloaded.setIsVisiable(true);
						menu.addMenuItem(catalog_downloaded);

						SwipeMenuItem view = new SwipeMenuItem(mContext);
						view.setId(40000);
						view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						view.setIcon(R.drawable.ic_list_view);
						view.setIsVisiable(true);
						menu.addMenuItem(view);
						break;
					case CONTENT_TYPE_CATALOG_NOT_DOWNLOADED:
						SwipeMenuItem catalog_not_download = new SwipeMenuItem(mContext);
						catalog_not_download.setId(30000);
						catalog_not_download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						catalog_not_download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						catalog_not_download.setIcon(R.drawable.ic_list_trash);
						catalog_not_download.setIsVisiable(false);
						menu.addMenuItem(catalog_not_download);

						SwipeMenuItem catalog_view = new SwipeMenuItem(mContext);
						catalog_view.setId(40000);
						catalog_view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						catalog_view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						catalog_view.setIcon(R.drawable.ic_list_view);
						catalog_view.setIsVisiable(true);
						menu.addMenuItem(catalog_view);
						break;
					case CONTENT_TYPE_MAUNAL_DOWNLOADED:
						SwipeMenuItem manual_download = new SwipeMenuItem(mContext);
						manual_download.setId(30000);
						manual_download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						manual_download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						manual_download.setIcon(R.drawable.ic_list_trash);
						manual_download.setIsVisiable(true);
						menu.addMenuItem(manual_download);

						SwipeMenuItem manual_view = new SwipeMenuItem(mContext);
						manual_view.setId(40000);
						manual_view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						manual_view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						manual_view.setIcon(R.drawable.ic_list_view);
						manual_view.setIsVisiable(true);
						menu.addMenuItem(manual_view);
						break;
					case CONTENT_TYPE_MAUNAL_NOT_DOWNLOADED:
						SwipeMenuItem manual_not_download = new SwipeMenuItem(mContext);
						manual_not_download.setId(30000);
						manual_not_download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
						manual_not_download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						manual_not_download.setIcon(R.drawable.ic_list_trash);
						manual_not_download.setIsVisiable(false);
						menu.addMenuItem(manual_not_download);

						SwipeMenuItem manual_not_view = new SwipeMenuItem(mContext);
						manual_not_view.setId(40000);
						manual_not_view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
						manual_not_view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
						manual_not_view.setIcon(R.drawable.ic_list_view);
						manual_not_view.setIsVisiable(true);
						menu.addMenuItem(manual_not_view);
						break;
				}

			}
		};
		listview.setMenuCreator(creator);

		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenuView parent, SwipeMenu menu, int index) {
				Nexxoo.saveContentId(mContext, mBaseEntityList.get(position).getContentId());
				int contentTypeId = menu.getViewType();
				if (contentTypeId == 3) {//video
					switch (index) {
						case 50000:
							if (mFileStorgeHelper.isContentDownloaded(mBaseEntityList.get(position)
									.getFileName())) {//downloaded
								File video = new File(mFileStorgeHelper.getDownloadAbsolutePath
										(mBaseEntityList
												.get(position).getFileName()));
								video.delete();
								/**
								 * delete content from DB
								 */
								ContentDBHelper db = new ContentDBHelper(mContext);
								db.deleteContent(mBaseEntityList.get(position).getContentId());

								LinearLayout imageLayout = (LinearLayout) parent
										.findViewById(new
												Integer(50000));
								ImageView image = (ImageView) imageLayout.getChildAt(0);
								image.setImageResource(R.drawable.ic_list_download);
							} else {// not downloaded
								/**
								 * add download content to local DB
								 */
								Video video = (Video) mBaseEntityList.get(position);
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
							String url = mBaseEntityList.get(position).getUrl();
							url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
							Intent i = new Intent(mContext, VideoActivity.class);
							i.putExtra(mContext.getString(R.string
									.video_activity_intent_url_extra), url);
							String name = mBaseEntityList.get(position).getFileName();
							i.putExtra("filename", name);
							i.putExtra("isVideoDownloaded", isVideoDownloaded);
							mContext.startActivity(i);
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

								LinearLayout image = (LinearLayout) parent.findViewById(new
										Integer(50000));
								image.setVisibility(View.GONE);
								//invisible trash icon
								break;
							case 50001:
								String filename = mBaseEntityList.get(position).getFileName();
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
//									listview.closeAllMenu();
//									DownloadAsyncTask task1 = new DownloadAsyncTask(mContext,
//											mBaseEntityList
//													.get(position).getUrl(), mBaseEntityList.get
//											(position).getFileName());
//									task1.execute();
								}
								break;
						}
					} else {// one button
						LinearLayout image = (LinearLayout) parent.findViewById(new
								Integer(50000));
						image.setVisibility(View.VISIBLE);

						String filename1 = mBaseEntityList.get(position).getFileName();
						if (mFileStorgeHelper.isContentDownloaded(filename1)) {
							/**
							 * not useful, one button means, no PDF downloaded.
							 */
//							File file = new File(mFileStorgeHelper.getDownloadAbsolutePath
//									(filename1));
//							Intent target = new Intent(Intent.ACTION_VIEW);
//							target.setDataAndType(Uri.fromFile(file), "application/pdf");
//							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//							Intent i = Intent.createChooser(target, mContext
//									.getString(R.string.open_pdf_file));
//							startActivity(i);
						} else {
							if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
								SwipeMenuLayout menuLayout = (SwipeMenuLayout)
										listview.getChildAt(position);
								menuLayout.smoothCloseMenu();
							}
							/**
							 * add download content to local DB
							 */
							switch (mBaseEntityList.get(position).getContentTypeId()) {
								case 1:
									Catalog catalog = (Catalog) mBaseEntityList.get(position);
									ContentDBHelper db = new ContentDBHelper(mContext);
									db.addContact(catalog);
									break;
								case 2:
									Manual manual = (Manual) mBaseEntityList.get(position);
									ContentDBHelper db1 = new ContentDBHelper(mContext);
									db1.addContact(manual);
									break;
							}
							BaseEntity base = mBaseEntityList.get(position);
							ContentDBHelper db = new ContentDBHelper(mContext);
							db.addContact(base);
							DownloadAsyncTask task1 = new DownloadAsyncTask(mContext,
									base.getUrl(), base.getFileName(),
									base.getmPictureList().isEmpty()?null:base.getmPictureList().get(0).getmUrl(),
									base.getContentId() + ".jpg", Global
									.DOWNLOAD_TASK_TYPE_PDF);
							task1.execute();
						}
					}
				}
				/*switch (index) {
					case 0:
						if (contentTypeId == CONTENT_TYPE_MAUNAL || contentTypeId == CONTENT_TYPE_CATALOG) {
							String filename = mBaseEntityList.get(position).getFileName();
							if (mFileStorgeHelper.isContentDownloaded(filename)) {
								File file = new File(mFileStorgeHelper.getDownloadAbsolutePath(filename));
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
								File file = new File(mFileStorgeHelper.getDownloadAbsolutePath
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
				}*/
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
					tmpList.add(mBaseEntity);


				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}
			for (int id : ids) {
				for (BaseEntity base : tmpList) {
					if (base != null && base.getContentId() == id)
						mBaseEntityList.add(base);
//					Log.d(Nexxoo.TAG, "History item lite content ids :" + base.getContentId());
				}
			}
		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

}