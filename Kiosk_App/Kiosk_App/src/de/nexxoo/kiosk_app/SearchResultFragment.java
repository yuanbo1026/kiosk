package de.nexxoo.kiosk_app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Manual;
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

/**
 * Created by b.yuan on 26.08.2015.
 */
public class SearchResultFragment extends android.support.v4.app.Fragment {
	private SwipeMenuListView mSwipeListView;
	private GridView mGridview;
	private List<BaseEntity> mEntityList = new ArrayList<BaseEntity>();
	private ViewSwitcher mViewSwitcher;
	private ImageButton b_grid;
	private ImageButton b_list;
	private int VIEW_GRID = 0;
	private int VIEW_LIST = 1;
	private View Header;

	private Context mContext;
	private FileStorageHelper mFileStorgeHelper;
	private int mFragmentType = -1;

	private int type;

	public static SearchResultFragment newInstance(Context context, int type) {
		SearchResultFragment f = new SearchResultFragment();
		Bundle args = new Bundle();
		args.putInt(context.getString(R.string.search_result_fragment_type), type);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		type = getArguments().getInt("type");
		mContext = this.getActivity();
		mFileStorgeHelper = new FileStorageHelper(mContext);
		mFragmentType = getArguments().getInt(mContext.getString(R.string
				.search_result_fragment_type));

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
					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);
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
					ImageButton mBList = (ImageButton) getActivity().findViewById(R.id.manual_b_list);
					ImageButton mBGrid = (ImageButton) getActivity().findViewById(R.id.manual_b_grid);
					mBList.setImageResource(R.drawable.ic_list);
					mBGrid.setImageResource(R.drawable.ic_grid_active);
				}
			}
		});


		mSwipeListView = (SwipeMenuListView) rootView.findViewById(R.id.manual_list);
		mGridview = (GridView) rootView.findViewById(R.id.manual_grid);

		Header = (View) rootView.findViewById(R.id.manual_header);
		Header.setVisibility(View.INVISIBLE);

		mGridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DownloadAsyncTask task = new DownloadAsyncTask(mContext,
						mEntityList
								.get(position).getUrl(), mEntityList.get
						(position).getFileName());
				task.execute();
			}
		});
		/**
		 * init swipe listview
		 */
//		initSwipeListView(Global.isNormalScreenSize);

		NexxooWebservice.getContent(true, 0, 10, Global.MANUAL_DATABASE_ENTITY_TYPE, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int count = json.getInt("count");
					prepareListData(json);

					/*ManualGridViewAdapter gridAdapter = new ManualGridViewAdapter
							(getActivity(), R.layout.manual_gridview_item, mEntityList);
					mGridview.setAdapter(gridAdapter);

					ManualListAdapter listAdapter = new ManualListAdapter(getActivity
							(), Global.isNormalScreenSize ? R.layout
							.manual_listview_item : R.layout.manual_listview_item_big, mEntityList);
					mSwipeListView.setAdapter(listAdapter);*/

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
	/*private void initSwipeListView(final Boolean isNormal) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(context);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
				openItem.setWidth(dp2px(isNormal ? 90 : 120));
				openItem.setIcon(R.drawable.ic_list_download);
				menu.addMenuItem(openItem);

				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
				deleteItem.setWidth(dp2px(isNormal ? 90 : 120));
				deleteItem.setIcon(R.drawable.ic_list_view);
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
						DownloadAsyncTask task = new DownloadAsyncTask(context,
								manualList
										.get(position).getUrl(), manualList.get
								(position).getFileName(), menu);
						task.execute();
						break;
					case 1:
						String filename = manualList.get(position).getFileName();
						if (fileHelper.isContentDownloaded(filename)) {
							File file = new File(fileHelper.getFileAbsolutePath(filename));
							Intent target = new Intent(Intent.ACTION_VIEW);
							target.setDataAndType(Uri.fromFile(file), "application/pdf");
							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

							Intent i = Intent.createChooser(target, "Open File");
							startActivity(i);
						} else {
							Toast.makeText(context, "There is no such PDF file.", Toast
									.LENGTH_LONG)
									.show();
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
	}*/
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	private void prepareListData(JSONObject json) {
		try {
			int count = json.getInt("count");
			Manual manual = null;
			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content" + i);
					manual = new Manual(jsonContentObj);
//					manualList.add(manual);

				} catch (Exception e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

}