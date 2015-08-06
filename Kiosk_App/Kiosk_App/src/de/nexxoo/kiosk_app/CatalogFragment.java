package de.nexxoo.kiosk_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ViewSwitcher;
import de.nexxoo.kiosk_app.entity.Catalog;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {

	private ListView listview;
	private GridView gridview;
	private List<Catalog> catalogList = new ArrayList<Catalog>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.catalog_fragment, container, false);
		listview = (ListView) rootView.findViewById(R.id.catalog_list);
		gridview = (GridView) rootView.findViewById(R.id.catalog_grid);

		gridview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		CatalogListAdapter gridviewAdapter = new CatalogListAdapter(getActivity(), R.layout.catalog_gridview_item, getCatalogList());
		gridview.setAdapter(gridviewAdapter);
		CatalogListAdapter listAdapter = new CatalogListAdapter(getActivity(), R.layout.catalog_listview_item, getCatalogList());
		listview.setAdapter(listAdapter);

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.catalog_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);

		b_grid = (ImageButton) rootView.findViewById(R.id.catalog_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.catalog_b_list);

		b_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_GRID) {
					mViewSwitcher.showNext();

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.catalog_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.catalog_b_grid);

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

					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.catalog_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.catalog_b_grid);

					mBList.setBackgroundResource(R.color.RealWhite);
					mBList.setImageResource(R.drawable.ic_stationlist_blue_light);

					mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
					mBGrid.setImageResource(R.drawable.ic_grid_white);

				}
			}
		});
		return rootView;
	}

	private List<Catalog> getCatalogList() {
		String[] catalog_name = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
		for (String str : catalog_name) {
			Catalog catalog = new Catalog();
			catalog.setmName("catalog " + str);
			catalogList.add(catalog);
		}
		return catalogList;
	}
}
