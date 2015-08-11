package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.entity.Manual;

import java.util.ArrayList;
import java.util.List;

public class ManualFragment extends Fragment {
	private ListView listview;
	private GridView gridview;
	private List<Manual> manualList = new ArrayList<Manual>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;

	private Context context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		View rootView = inflater.inflate(R.layout.manual_fragment, container, false);
		listview = (ListView) rootView.findViewById(R.id.manual_list);
		gridview = (GridView) rootView.findViewById(R.id.manual_grid);

		ManualListAdapter gridAdapter = new ManualListAdapter(getActivity(), R.layout.manual_gridview_item, getManualList());
		gridview.setAdapter(gridAdapter);

		/*OptimizeGridAdapter gridAdapter = new OptimizeGridAdapter(getActivity(), R.layout.manual_gridview_item, getManualList());
		gridview.setAdapter(gridAdapter);*/
		ManualListAdapter listAdapter = new ManualListAdapter(getActivity(), R.layout.manual_listview_item, getManualList());
		listview.setAdapter(listAdapter);

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, ManualActivity.class);
				startActivity(intent);
			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, ManualActivity.class);
				startActivity(intent);
			}
		});

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.manual_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);

		b_grid = (ImageButton) rootView.findViewById(R.id.manual_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.manual_b_list);

		b_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_GRID) {
					mViewSwitcher.showNext();

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);

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

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);

					mBList.setBackgroundResource(R.color.RealWhite);
					mBList.setImageResource(R.drawable.ic_stationlist_blue_light);

					mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
					mBGrid.setImageResource(R.drawable.ic_grid_white);

				}
			}
		});
		return rootView;
	}

	private List<Manual> getManualList() {
		String[] manual_name = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
		for (String str : manual_name) {
			Manual manual = new Manual();
			manual.setmName("Manual " + str);
			manualList.add(manual);
		}
		return manualList;
	}

}
