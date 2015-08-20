package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.layout.SwipeMenu;
import de.nexxoo.kiosk_app.layout.SwipeMenuCreator;
import de.nexxoo.kiosk_app.layout.SwipeMenuItem;
import de.nexxoo.kiosk_app.layout.SwipeMenuListView;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManualFragment extends Fragment {
	private SwipeMenuListView listview;
	private GridView gridview;
	private List<Manual> manualList = new ArrayList<Manual>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;
	private View Header;

	private Context context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		View rootView = inflater.inflate(R.layout.manual_fragment, container, false);
		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.manual_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);

		b_grid = (ImageButton) rootView.findViewById(R.id.manual_b_grid);
		b_list = (ImageButton) rootView.findViewById(R.id.manual_b_list);

		b_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_GRID) {
					mViewSwitcher.showNext();
					Header.setVisibility(View.VISIBLE);
					/*ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);

					mBList.setBackgroundResource(R.color.RadiothekBlueLight);
					mBList.setImageResource(R.drawable.ic_stationlist);

					mBGrid.setBackgroundResource(R.color.RadiothekWhite);
					mBGrid.setImageResource(R.drawable.ic_grid_blue_light);*/

				}
			}
		});

		b_grid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mViewSwitcher.getDisplayedChild() == VIEW_LIST) {
					mViewSwitcher.showPrevious();
					Header.setVisibility(View.INVISIBLE);
					/*ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);

					mBList.setBackgroundResource(R.color.RealWhite);
					mBList.setImageResource(R.drawable.ic_stationlist_blue_light);

					mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
					mBGrid.setImageResource(R.drawable.ic_grid_white);*/

				}
			}
		});
		listview = (SwipeMenuListView) rootView.findViewById(R.id.manual_list);
		gridview = (GridView) rootView.findViewById(R.id.manual_grid);
		gridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);
		Header = (View) rootView.findViewById(R.id.header);
		Header.setVisibility(View.INVISIBLE);

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, ManualActivity.class);
				intent.putExtra("filename", manualList.get(position).getFileName());
				intent.putExtra("name", manualList.get(position).getName());
				intent.putExtra("url", manualList.get(position).getUrl());
				startActivity(intent);
			}
		});
		/**
		 * init swipe listview
		 */
		initSwipeListView();

		NexxooWebservice.getContent(true, 0, 10, 2, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int count = json.getInt("count");
					prepareListData(json);
					ManualGridViewAdapter gridAdapter = new ManualGridViewAdapter
							(getActivity(), R.layout.manual_gridview_item, manualList);
					gridview.setAdapter(gridAdapter);

					ManualListAdapter listAdapter = new ManualListAdapter(getActivity
							(), Global.isNormalScreenSize ? R.layout
							.manual_listview_item : R.layout.manual_listview_item_big, manualList);
					listview.setAdapter(listAdapter);
					Log.d("KioskSuccess", "get content from DB: " + count);

				} catch (JSONException e) {
					Log.d("KioskError", "error!" + e.getMessage());
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Log.d("KioskError", "error!" + msg);
			}
		});



		return rootView;
	}

	/**
	 * initialize swipe listview component
	 */
	private void initSwipeListView() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						context);
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listview.setMenuCreator(creator);

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						Toast.makeText(context, "Open Manual", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(context, "Download Manual", Toast.LENGTH_SHORT)
								.show();
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

	private void download(ApplicationInfo item) {
		// delete app
		/*try {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.fromParts("package", item.packageName, null));
			startActivity(intent);
		} catch (Exception e) {
		}*/
	}

	private void open() {
		// open app
		/*Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
		}*/
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	/*private List<Manual> getManualList() {
		String[] manual_name = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
		for (String str : manual_name) {
			Manual manual = new Manual();
			manual.setName("Manual " + str);
			manualList.add(manual);
		}
		return manualList;
	}*/

	private void prepareListData(JSONObject json) {


		try {
			int count = json.getInt("count");
			Manual manual = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					manual = new Manual(jsonContentObj);
					manualList.add(manual);

				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

}