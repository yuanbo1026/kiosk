package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.nexxoo.kiosk_app.db.ContentDBHelper;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResultManualFragment extends Fragment implements UpdateSwipeListViewMenuItem{
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
	private DatabaseHandler dbHandler;

	private ManualListAdapter listAdapter;
	private ManualGridViewAdapter gridAdapter;

	public static SearchResultManualFragment newInstance(Context context,
													  List<Manual> entityList) {
		SearchResultManualFragment f = new SearchResultManualFragment();
		Bundle args = new Bundle();
		args.putSerializable(context.getString(R.string.search_result_manual_list),
				(Serializable)entityList);
		f.setArguments(args);
		return f;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		manualList = (List<Manual>) getArguments().getSerializable(context
				.getString(R.string
						.search_result_manual_list));
		fileHelper = new FileStorageHelper(context);
		dbHandler = new DatabaseHandler(context);

		View rootView = inflater.inflate(R.layout.manual_fragment, container, false);


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



		gridview = (GridView) rootView.findViewById(R.id.manual_grid);
		listview = (SwipeMenuListView) rootView.findViewById(R.id.manual_list);
		/**
		 * init swipe listview
		 */
		initSwipeListView(Global.isNormalScreenSize);

		gridAdapter = new ManualGridViewAdapter
				(getActivity(), R.layout.manual_gridview_item, manualList);
		gridview.setAdapter(gridAdapter);
		gridAdapter.setCallback(SearchResultManualFragment.this);
		gridview.setNumColumns(Global.isNormalScreenSize ? 1 : 2);
		TextView emptyView = new TextView(context);
		emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		emptyView.setText(Nexxoo.getStyledText(context, context.getString(R.string
				.search_no_result)));
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		emptyView.setVisibility(View.GONE);
		((ViewGroup)listview.getParent().getParent()).addView(emptyView);
		listview.setEmptyView(emptyView);
		listAdapter = new ManualListAdapter(getActivity
				(), Global.isNormalScreenSize ? R.layout
				.manual_listview_item : R.layout.manual_listview_item_big, manualList);
		listview.setAdapter(listAdapter);
		Header = (View) rootView.findViewById(R.id.manual_header);
		Header.setVisibility(View.INVISIBLE);
		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.manual_viewswitcher);
		mViewSwitcher.setDisplayedChild(0);
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

		// step 2. listener item click event
		listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position,SwipeMenuView parent,
										   SwipeMenu menu, int id) {
				Nexxoo.saveContentId(context, manualList.get(position).getContentId());
				if (fileHelper.isContentDownloaded(manualList.get(position).getFileName())) {
					//two button on item
					switch (id) {
						case 50000://trash button
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
							updateGridViewItemIcon(position);
							break;
						case 50001://watch button
							String filename1 = manualList.get(position).getFileName();
							if (fileHelper.isContentDownloaded(filename1)) {
								File file = new File(fileHelper.getDownloadAbsolutePath
										(filename1));
								Intent target = new Intent(Intent.ACTION_VIEW);
								target.setDataAndType(Uri.fromFile(file), "application/pdf");
								target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

								View view = listview.getChildAt(position);

								Intent i = Intent.createChooser(target, context
										.getString(R.string.open_pdf_file));
								startActivity(i);
							} else {
								if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
									SwipeMenuLayout menuLayout = (SwipeMenuLayout)
											listview.getChildAt(position);
									menuLayout.smoothCloseMenu();
								}
								/**
								 * add download content to local DB
								 */
								Manual manual1 = manualList.get(position);
								ContentDBHelper db1 = new ContentDBHelper(context);
								db1.addContact(manual1);
								DownloadAsyncTask task1 = new DownloadAsyncTask(context,
										manualList
												.get(position).getUrl(), manualList.get
										(position).getFileName(),manualList.get
										(position).getmPictureList().get(0).getmUrl(),manualList.get
										(position).getContentId()+".jpg", Global
										.DOWNLOAD_TASK_TYPE_PDF);
								task1.execute();
							}
							break;
					}
				} else {// one button on item
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

						View view = listview.getChildAt(position);

						Intent i = Intent.createChooser(target, context
								.getString(R.string.open_pdf_file));
						startActivity(i);
					} else {
						if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
							SwipeMenuLayout menuLayout = (SwipeMenuLayout)
									listview.getChildAt(position);
							menuLayout.smoothCloseMenu();
						}
						/**
						 * add download content to local DB
						 */
						Manual manual = manualList.get(position);
						ContentDBHelper db = new ContentDBHelper(context);
						db.addContact(manual);
						DownloadAsyncTask task = new DownloadAsyncTask(context,
								manualList.get(position).getUrl(), manualList.get
								(position).getFileName(),manualList.get
								(position).getmPictureList().get(0).getmUrl(),manualList.get
								(position).getContentId()+".jpg", Global
								.DOWNLOAD_TASK_TYPE_PDF);
						task.execute();
					}
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
				view.requestFocus();
			}
		});
		listview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				listview.closeAllMenu();
			}
		});
	}

	private void updateGridViewItemIcon(int position){
		LinearLayout griditemLayout = (LinearLayout) gridview.getChildAt(0);
		ImageView trash_icon = (ImageView) griditemLayout.findViewById(R.id
				.manual_grid_item_trash_button);
		trash_icon.setVisibility(View.INVISIBLE);
	}

	@Override
	public void updateListViewItemIcon(int position,boolean isVisible) {
		listview.updateMenuIcon(position,isVisible);
	}
}
