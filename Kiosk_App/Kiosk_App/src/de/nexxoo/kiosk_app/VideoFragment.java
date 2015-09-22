package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.entity.Video;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoFragment extends Fragment implements UpdateSwipeListViewMenuItem{
	private SwipeMenuListView listview;
	private GridView gridview;
	private List<Video> videoList = new ArrayList<Video>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;
	private View Header;

	private Context context;
	private VideoListAdapter listAdapter;
	private VideoGridViewAdapter gridAdapter;

	private boolean isVideoDownloaded;
	private FileStorageHelper fileHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		fileHelper = new FileStorageHelper(context);

		View rootView = inflater.inflate(R.layout.video_fragment, container, false);

		b_grid = (ImageButton) rootView.findViewById(R.id.video_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.video_b_list);

		listview = (SwipeMenuListView) rootView.findViewById(R.id.video_list);
		gridview = (GridView) rootView.findViewById(R.id.video_grid);
		gridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.video_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);

		Header = (View) rootView.findViewById(R.id.video_header);
		Header.setVisibility(View.INVISIBLE);

		b_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_GRID) {
					mViewSwitcher.showNext();
					Header.setVisibility(View.VISIBLE);
					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.video_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.video_b_grid);
					mBList.setImageResource(R.drawable.ic_list_active);
					mBGrid.setImageResource(R.drawable.ic_grid);
				}
			}
		});

		b_grid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_LIST) {
					mViewSwitcher.showPrevious();
					Header.setVisibility(View.INVISIBLE);
					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.video_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.video_b_grid);
					mBList.setImageResource(R.drawable.ic_list);
					mBGrid.setImageResource(R.drawable.ic_grid_active);

				}
			}
		});

		initSwipeListView(Global.isNormalScreenSize);

		NexxooWebservice.getContent(true, 0, -1, Global.VIDEO_DATABASE_ENTITY_TYPE, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int count = json.getInt("count");
					Log.d(Nexxoo.TAG, "get Video list size is : " + count);
					prepareListData(json);
					gridAdapter = new VideoGridViewAdapter
							(getActivity(), R.layout.video_gridview_item,
									videoList);
					gridview.setAdapter(gridAdapter);
					gridAdapter.setCallback(VideoFragment.this);
					listAdapter = new VideoListAdapter(getActivity
							(), Global.isNormalScreenSize ? R.layout
							.video_listview_item : R.layout.video_listview_item_big, videoList);
					listview.setAdapter(listAdapter);

				} catch (JSONException e) {
					Log.d("KioskError", "Error!" + e.getMessage());
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Log.d("KioskError", "Error!" + msg);
			}
		});


		return rootView;
	}

	/**
	 * initialize swipe listview component
	 */
	private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem download = new SwipeMenuItem(context);
				download.setId(30000);
				download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
				download.setWidth(Nexxoo.dp2px(context, isNormal ? 90 : 120));
				download.setIcon(menu.getViewType() > 0?R.drawable.ic_list_trash:R
						.drawable.ic_list_download);
				download.setIsVisiable(true);
				menu.addMenuItem(download);

				SwipeMenuItem view = new SwipeMenuItem(context);
				view.setId(40000);
				view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
				view.setWidth(Nexxoo.dp2px(context, isNormal ? 90 : 120));
				view.setIcon(R.drawable.ic_list_play);
				view.setIsVisiable(true);
				menu.addMenuItem(view);
			}
		};
		// set creator
		listview.setMenuCreator(creator);

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenuView parent, SwipeMenu menu, int index) {
				Nexxoo.saveContentId(context, videoList.get(position).getContentId());
				switch (index) {
					case 50000:
						if (fileHelper.isContentDownloaded(videoList.get(position)
								.getFileName())) {//downloaded
							File video = new File(fileHelper.getFileAbsolutePath
									(videoList
											.get(position).getFileName()));
							video.delete();
							LinearLayout imageLayout = (LinearLayout) parent
									.findViewById(new
											Integer(50000));
							ImageView image =(ImageView)imageLayout.getChildAt(0);
							image.setImageResource(R.drawable.ic_list_download);
							updateGridViewItemIcon(position, false);
						} else {// not downloaded
							updateGridViewItemIcon(position,true);
							DownloadAsyncTask task = new DownloadAsyncTask(context,
									videoList
											.get(position).getUrl(), videoList.get
									(position).getFileName());
							task.execute();
							LinearLayout imageLayout = (LinearLayout) parent
									.findViewById(new
											Integer(50000));
							ImageView image =(ImageView)imageLayout.getChildAt(0);
							image.setImageResource(R.drawable.ic_list_trash);
						}
						break;
					case 50001://play button
						isVideoDownloaded = fileHelper.isContentDownloaded(videoList
								.get(position).getFileName());
						String url = videoList.get(position).getUrl();
						url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
						Intent i = new Intent(context, VideoActivity.class);
						i.putExtra(context.getString(R.string
								.video_activity_intent_url_extra), url);
						String name = videoList.get(position).getFileName();
						i.putExtra("filename", name);
						i.putExtra("isVideoDownloaded", isVideoDownloaded);
						context.startActivity(i);
						break;
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

	private void prepareListData(JSONObject json) {
		try {
			int count = json.getInt("count");
			Video video = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					video = new Video(jsonContentObj);
					videoList.add(video);

				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}
			if (videoList.size() > 0) {
				Collections.sort(videoList, new Comparator<Video>() {
					@Override
					public int compare(final Video object1, final Video object2) {
						return object1.getName().compareTo(object2.getName());
					}
				});
			}
		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

	private void updateGridViewItemIcon(int position,boolean isDownloaded){
		LinearLayout griditemLayout = (LinearLayout) gridview.getChildAt(0);
		ImageView download_icon = (ImageView) griditemLayout.findViewById(R.id
				.video_gridview_item_download_button);
		download_icon.setImageResource(isDownloaded?R.drawable.ic_grid_trash:R
				.drawable.ic_grid_download);
	}

	@Override
	public void updateListViewItemIcon(int position,boolean isVisible) {
		listview.updateVideoMenuIcon(position,isVisible);
	}
}
