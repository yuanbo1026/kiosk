package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.entity.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
	private ListView listview;
	private GridView gridview;
	private List<Video> videoList = new ArrayList<Video>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		View rootView = inflater.inflate(R.layout.video_fragment, container, false);
		listview = (ListView) rootView.findViewById(R.id.video_list);
		gridview = (GridView) rootView.findViewById(R.id.video_grid);

		gridview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		VideoListAdapter gridviewAdapter = new VideoListAdapter(getActivity(), R.layout.video_gridview_item, getVideoList());
		gridview.setAdapter(gridviewAdapter);
		VideoListAdapter listAdapter = new VideoListAdapter(getActivity(), R.layout.video_listview_item, getVideoList());
		listview.setAdapter(listAdapter);

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.video_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, VideoActivity.class);
				startActivity(intent);
			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, VideoActivity.class);
				startActivity(intent);
			}
		});

		b_grid = (ImageButton) rootView.findViewById(R.id.video_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.video_b_list);

		b_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_GRID) {
					mViewSwitcher.showNext();

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.video_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.video_b_grid);

					mBList.setBackgroundResource(R.color.RadiothekBlueLight);
					mBList.setImageResource(R.drawable.ic_stationlist);

					mBGrid.setBackgroundResource(R.color.RadiothekWhite);
					mBGrid.setImageResource(R.drawable.ic_grid_blue_light);

				}
			}
		});

		b_grid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_LIST) {
					mViewSwitcher.showPrevious();

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.video_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.video_b_grid);

					mBList.setBackgroundResource(R.color.RealWhite);
					mBList.setImageResource(R.drawable.ic_stationlist_blue_light);

					mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
					mBGrid.setImageResource(R.drawable.ic_grid_white);

				}
			}
		});
		return rootView;
	}

	private List<Video> getVideoList() {
		String[] video_name = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
		for (String str : video_name) {
			Video video = new Video();
			video.setmName("Video " + str);
			videoList.add(video);
		}
		return videoList;
	}
}
