package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResultVideoFragment extends Fragment implements UpdateSwipeListViewMenuItem{
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
	private DatabaseHandler dbHandler;

	public static SearchResultVideoFragment newInstance(Context context,
													  List<Video> entityList) {
		SearchResultVideoFragment f = new SearchResultVideoFragment();
		Bundle args = new Bundle();
		args.putSerializable(context.getString(R.string.search_result_video_list),
				(Serializable)entityList);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		videoList = (List<Video>) getArguments().getSerializable(context
				.getString(R.string
						.search_result_video_list));
		fileHelper = new FileStorageHelper(context);
		dbHandler = new DatabaseHandler(context);

		View rootView = inflater.inflate(R.layout.video_fragment, container, false);

		b_grid = (ImageButton) rootView.findViewById(R.id.video_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.video_b_list);

		listview = (SwipeMenuListView) rootView.findViewById(R.id.video_list);
		gridview = (GridView) rootView.findViewById(R.id.video_grid);
		gridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);

		gridAdapter = new VideoGridViewAdapter
				(getActivity(), R.layout.video_gridview_item,
						videoList);
		gridview.setAdapter(gridAdapter);
		gridAdapter.setCallback(SearchResultVideoFragment.this);
		listAdapter = new VideoListAdapter(getActivity
				(), Global.isNormalScreenSize ? R.layout
				.video_listview_item : R.layout.video_listview_item_big, videoList);
		listview.setAdapter(listAdapter);
		TextView emptyView = new TextView(context);
		emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		emptyView.setText(Nexxoo.getStyledText(context,context.getString(R.string
				.search_no_result)));
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		emptyView.setVisibility(View.GONE);
		((ViewGroup)listview.getParent().getParent()).addView(emptyView);
		listview.setEmptyView(emptyView);

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
							File video = new File(fileHelper.getDownloadAbsolutePath
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
				/*if (fileHelper.isContentDownloaded(videoList.get(position)
						.getFileName())) {
					switch (index) {
						case 50000:
							File video = new File(fileHelper.getDownloadAbsolutePath
									(videoList
											.get(position).getFileName()));
							video.delete();
							LinearLayout image = (LinearLayout) parent.findViewById(new
									Integer(50000));
							image.setVisibility(View.GONE);
							break;
						case 50001:
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

				} else {
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
				}*/
				return false;

			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listview.smoothOpenMenu(position);
//				Log.e(Nexxoo.TAG, "Get SwipeListview Item's view type: " + listAdapter.getItemViewType(position));
			}
		});
		listview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				listview.closeAllMenu();
			}
		});
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
