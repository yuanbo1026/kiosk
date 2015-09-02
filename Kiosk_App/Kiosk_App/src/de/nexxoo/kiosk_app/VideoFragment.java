package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.layout.SwipeMenu;
import de.nexxoo.kiosk_app.layout.SwipeMenuCreator;
import de.nexxoo.kiosk_app.layout.SwipeMenuItem;
import de.nexxoo.kiosk_app.layout.SwipeMenuListView;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
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

	private boolean isVideoDownloaded;
	private FileStorageHelper fileHelper;
	private DatabaseHandler dbHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		fileHelper = new FileStorageHelper(context);
		dbHandler = new DatabaseHandler(context);

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
					Log.d(Nexxoo.TAG,"get Video list size is : "+count);
					prepareListData(json);
					VideoGridViewAdapter gridAdapter = new VideoGridViewAdapter
							(getActivity(), R.layout.video_gridview_item,
									videoList);
					gridview.setAdapter(gridAdapter);

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

	/*private List<Video> getVideoList() {
		String[] video_name = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
		for (String str : video_name) {
			Video video = new Video();
			video.setmName("Video " + str);
			videoList.add(video);
		}
		return videoList;
	}*/

	/**
	 * initialize swipe listview component
	 */
	private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				int viewType = menu.getViewType();
				SwipeMenuItem openItem = new SwipeMenuItem(context);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
				openItem.setWidth(dp2px(isNormal ? 90 : 120));
				openItem.setIcon(R.drawable.ic_list_download);
				menu.addMenuItem(openItem);

				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
				deleteItem.setWidth(dp2px(isNormal ? 90 : 120));
				deleteItem.setIcon(R.drawable.ic_list_play);
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listview.setMenuCreator(creator);

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				Nexxoo.saveContentId(context,videoList.get(position).getContentId());
				switch (index) {
					case 0:
						DownloadAsyncTask task = new DownloadAsyncTask(context,
								videoList
										.get(position).getUrl(), videoList.get
								(position).getFileName());
						task.execute();
						dbHandler.addContent(videoList.get(position).getContentId());
						break;
					case 1:
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
						dbHandler.addContent(videoList.get(position).getContentId());
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
				Log.e(Nexxoo.TAG, "Get SwipeListview Item's view type: " + listAdapter.getItemViewType(position));
			}
		});
		listview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				listview.closeAllMenu();
			}
		});
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
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

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}
}
