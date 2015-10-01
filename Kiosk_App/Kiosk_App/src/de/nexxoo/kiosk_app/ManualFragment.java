package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.db.ContentDBHelper;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManualFragment extends Fragment implements UpdateSwipeListViewMenuItem {
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

		/**
		 * init swipe listview
		 */
		initSwipeListView(Global.isNormalScreenSize);

		NexxooWebservice.getContent(true, 0, -1, Global.MANUAL_DATABASE_ENTITY_TYPE,
				new OnJSONResponse() {
					@Override
					public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json);
							gridAdapter = new ManualGridViewAdapter
									(getActivity(), R.layout.manual_gridview_item, manualList);
							gridview.setAdapter(gridAdapter);
							gridAdapter.setCallback(ManualFragment.this);
							listAdapter = new ManualListAdapter(getActivity
									(), Global.isNormalScreenSize ? R.layout
									.manual_listview_item : R.layout.manual_listview_item_big, manualList);
							listview.setAdapter(listAdapter);
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
				download.setIcon(R.drawable.ic_list_trash);
				download.setIsVisiable(menu.getViewType() > 0);
				menu.addMenuItem(download);

				SwipeMenuItem view = new SwipeMenuItem(context);
				view.setId(40000);
				view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
				view.setWidth(Nexxoo.dp2px(context, isNormal ? 90 : 120));
				view.setIcon(R.drawable.ic_list_view);
				view.setIsVisiable(true);
				menu.addMenuItem(view);
			}
		};
		// set creator
		listview.setMenuCreator(creator);
		listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
			}

			@Override
			public void onSwipeEnd(int position) {
			}
		});

		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenuView parent, SwipeMenu menu, int index) {
				Nexxoo.saveContentId(context, manualList.get(position).getContentId());
				if (fileHelper.isContentDownloaded(manualList.get(position)
						.getFileName())) {//two buttons
					switch (index) {
						case 50000:
							File manual = new File(fileHelper.getDownloadAbsolutePath
									(manualList
											.get(position).getFileName()));
							manual.delete();
							/**
							 * delete content from DB
							 */
							ContentDBHelper db = new ContentDBHelper(context);
							db.deleteContent(manualList.get(position).getContentId());


							LinearLayout image = (LinearLayout) parent.findViewById(new
									Integer(50000));
							image.setVisibility(View.GONE);
							//invisible trash icon
							updateGridViewItemIcon(position);
							break;
						case 50001:
							String filename = manualList.get(position).getFileName();
							if (fileHelper.isContentDownloaded(filename)) {
								listview.closeAllMenu();
								File file = new File(fileHelper.getDownloadAbsolutePath(filename));
								Intent target = new Intent(Intent.ACTION_VIEW);
								target.setDataAndType(Uri.fromFile(file), "application/pdf");
								target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								Intent i = Intent.createChooser(target, context
										.getString(R.string.open_pdf_file));
								startActivity(i);
							}
							break;
					}
				} else {// one button
					LinearLayout image = (LinearLayout) parent.findViewById(new
							Integer(50000));
					image.setVisibility(View.VISIBLE);

					String filename1 = manualList.get(position).getFileName();
					if (fileHelper.isContentDownloaded(filename1)) {
						File file = new File(fileHelper.getDownloadAbsolutePath
								(filename1));
						Intent target = new Intent(Intent.ACTION_VIEW);
						target.setDataAndType(Uri.fromFile(file), "application/pdf");
						target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						Intent i = Intent.createChooser(target, context
								.getString(R.string.open_pdf_file));
						startActivity(i);
					} else {
						/**
						 * add download content to local DB
						 */
						Manual manual = manualList.get(position);
						ContentDBHelper db = new ContentDBHelper(context);
						db.addContact(manual);

						if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
							SwipeMenuLayout menuLayout = (SwipeMenuLayout)
									listview.getChildAt(position);
							menuLayout.smoothCloseMenu();
						}

						DownloadAsyncTask task = new DownloadAsyncTask(context,
								manual.getUrl(),
								manual.getFileName(),
								manual.getmPictureList().isEmpty()?null:manual.getmPictureList().get(0)
										.getmUrl(),
								manual.getContentId()+".jpg", Global
								.DOWNLOAD_TASK_TYPE_PDF);
						task.execute();
					}
				}
				gridAdapter.notifyDataSetChanged();
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
			/**
			 * sort list by alphabet
			 */
			if (manualList.size() > 0) {
				Collections.sort(manualList, new Comparator<Manual>() {
					@Override
					public int compare(final Manual object1, final Manual object2) {
						return object1.getName().compareTo(object2.getName());
					}
				});
			}

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
	}

	private void updateGridViewItemIcon(int position) {
		LinearLayout griditemLayout = (LinearLayout) gridview.getChildAt(0);
		ImageView trash_icon = (ImageView) griditemLayout.findViewById(R.id
				.manual_grid_item_trash_button);
		trash_icon.setVisibility(View.INVISIBLE);
	}

	@Override
	public void updateListViewItemIcon(int position, boolean isVisible) {
		listview.updateMenuIcon(position, isVisible);
	}
}
