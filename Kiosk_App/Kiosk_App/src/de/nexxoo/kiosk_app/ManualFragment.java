package de.nexxoo.kiosk_app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.entity.Manual;
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
	private FileStorageHelper fileHelper;

	private ManualListAdapter listAdapter;
	private ManualGridViewAdapter gridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		fileHelper = new FileStorageHelper(context);

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


		listview = (SwipeMenuListView) rootView.findViewById(R.id.manual_list);
		gridview = (GridView) rootView.findViewById(R.id.manual_grid);
		gridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);
		Header = (View) rootView.findViewById(R.id.manual_header);
		Header.setVisibility(View.INVISIBLE);

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = manualList.get(position).getFileName();
				if (fileHelper.isContentDownloaded(filename)) {
					File file = new File(fileHelper.getFileAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file), "application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent i = Intent.createChooser(target, "Open File");
					try {
						context.startActivity(i);
					} catch (ActivityNotFoundException e) {
					}
				} else {
					ImageView mImageView = (ImageView) view.findViewById(R.id
							.manual_grid_item_trash_button);
					DownloadAsyncTask task = new DownloadAsyncTask(context,
							manualList
									.get(position).getUrl(), manualList.get
							(position).getFileName(), mImageView, Global.DOWNLOAD_TASK_TYPE_PDF);
					task.execute();
				}

			}
		});
		/**
		 * init swipe listview
		 */
		initSwipeListView(Global.isNormalScreenSize);

		NexxooWebservice.getContent(true, 0, 10, Global.MANUAL_DATABASE_ENTITY_TYPE, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int count = json.getInt("count");
					Log.d(Nexxoo.TAG, "get manual list size is : " + count);
					prepareListData(json);

					gridAdapter = new ManualGridViewAdapter
							(getActivity(), R.layout.manual_gridview_item, manualList);
					gridview.setAdapter(gridAdapter);

					listAdapter = new ManualListAdapter(getActivity
							(), Global.isNormalScreenSize ? R.layout
							.manual_listview_item : R.layout.manual_listview_item_big, manualList);
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
				/**
				 * 0 : menu items for PDF
				 * 1 : menu items for Video
				 */
				/*switch (menu.getViewType()){
					case 0:
						break;
					case 1:
						break;
					default:


				}*/
				SwipeMenuItem download = new SwipeMenuItem(context);
				download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
				download.setWidth(Nexxoo.dp2px(context,isNormal ? 90 : 120));
				download.setIcon(R.drawable.ic_list_download);
				menu.addMenuItem(download);

				SwipeMenuItem view = new SwipeMenuItem(context);
				view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
				view.setWidth(Nexxoo.dp2px(context, isNormal ? 90 : 120));
				view.setIcon(R.drawable.ic_list_view);
				menu.addMenuItem(view);


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
						String filename = manualList.get(position).getFileName();
						if (fileHelper.isContentDownloaded(filename)) {
							File file = new File(fileHelper.getFileAbsolutePath(filename));
							Intent target = new Intent(Intent.ACTION_VIEW);
							target.setDataAndType(Uri.fromFile(file), "application/pdf");
							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

							View view = listview.getChildAt(position);

							Intent i = Intent.createChooser(target, "Open File");
							startActivity(i);
						} else {
							if(listview.getChildAt(position) instanceof SwipeMenuLayout){
								SwipeMenuLayout menuLayout = (SwipeMenuLayout)
										listview.getChildAt(position);
								menuLayout.smoothCloseMenu();
							}
							DownloadAsyncTask task = new DownloadAsyncTask(context,
									manualList
											.get(position).getUrl(), manualList.get
									(position).getFileName(), Global
									.DOWNLOAD_TASK_TYPE_PDF);
							task.execute();

							/*SwipeMenuItem menuItem = menu.getMenuItem(0);
							menuItem.setIcon(R.drawable.ic_list_trash);
							listAdapter.notifyDataSetChanged();
							gridAdapter.notifyDataSetChanged();*/
						}
						break;
					case 1:
						String filename1 = manualList.get(position).getFileName();
						if (fileHelper.isContentDownloaded(filename1)) {
							File file = new File(fileHelper.getFileAbsolutePath
									(filename1));
							Intent target = new Intent(Intent.ACTION_VIEW);
							target.setDataAndType(Uri.fromFile(file), "application/pdf");
							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

							View view = listview.getChildAt(position);

							Intent i = Intent.createChooser(target, "Open File");
							startActivity(i);
						} else {
							if(listview.getChildAt(position) instanceof SwipeMenuLayout){
								SwipeMenuLayout menuLayout = (SwipeMenuLayout)
										listview.getChildAt(position);
								menuLayout.smoothCloseMenu();
							}
							DownloadAsyncTask task1 = new DownloadAsyncTask(context,
									manualList
											.get(position).getUrl(), manualList.get
									(position).getFileName(), Global
									.DOWNLOAD_TASK_TYPE_PDF);
							task1.execute();

							/*SwipeMenuItem menuItem = menu.getMenuItem(0);
							menuItem.setIcon(R.drawable.ic_list_trash);
							listAdapter.notifyDataSetChanged();
							gridAdapter.notifyDataSetChanged();*/
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
	}



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
