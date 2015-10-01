package de.nexxoo.kiosk_app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.astuetz.PagerSlidingTabStrip;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
	private List<Fragment> fragmentList;
	private FragmentAdapter fragmentAdapter;
	private ViewPager pager;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mTitles;

	private String[] mFragmentTitles;
	private Context context;
	private ActionBar actionbar;
	private MenuItem searchMenuItem;
	private SearchView searchView;

	@Override
	protected void onStart() {
		super.onStart();
		if (!Misc.isOnline(this)) {
			new AlertDialog.Builder(this)
					.setMessage(this.getResources().getString(R.string.no_wifi_message))
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(MainActivity.this, HistoryActivity.class);
							i.putExtra(getString(R.string.wifi_status), false);
							startActivity(i);
							Global.isNoticed = true;
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.custom_actionbar);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		setContentView(R.layout.drawer_container);

		mTitle = mDrawerTitle = getTitle();
		mTitles = getResources().getStringArray(R.array.drawer_titles);
		mFragmentTitles = getResources().getStringArray(R.array.fragment_titles);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_menu_white_36dp,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_title,  /* "open drawer" description for accessibility */
				R.string.drawer_title  /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {

			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerSlide(drawerView, 0);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, 0); // this disables the animation
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		/**
		 * begin: inflate content frame in container
		 */
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ManualFragment());
		fragmentList.add(new VideoFragment());
		fragmentList.add(new CatalogFragment());

		LayoutInflater inflater = getLayoutInflater();
		FrameLayout container = (FrameLayout) findViewById(R.id.content_frame);
		inflater.inflate(R.layout.activity_main, container);

		pager = (ViewPager) findViewById(R.id.pager);
		fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
				fragmentList, getActionBar());
		pager.setAdapter(fragmentAdapter);
		pager.setOffscreenPageLimit(2);

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setBackgroundColor(getResources().getColor(R.color.RealWhite));
		tabs.setIndicatorColor(getResources().getColor(R.color.Tabs_Indicator_Color));
		Typeface font = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
		tabs.setTypeface(font, Typeface.NORMAL);
		tabs.setViewPager(pager);
		/**
		 * end: inflate content frame in container
		 */
		/**
		 * register wifi status listener
		 */
		this.registerReceiver(this.myWifiReceiver,
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private BroadcastReceiver myWifiReceiver
			= new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = myConnManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {

			} else {
				new AlertDialog.Builder(context)
						.setMessage(context.getResources().getString(R.string.no_wifi_message))
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent i = new Intent(MainActivity.this, HistoryActivity.class);
								i.putExtra(getString(R.string.wifi_status), false);
								startActivity(i);
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		}
	};

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		mDrawerList.setItemChecked(position, false);
		mDrawerLayout.closeDrawer(mDrawerList);
		switch (position) {
			case 0:
				Intent imprint = new Intent(context, ImprintActivity.class);
				startActivity(imprint);
				break;
			case 1:
				Intent history = new Intent(context, HistoryActivity.class);
				history.putExtra(getString(R.string.from_menu_click),true);
				startActivity(history);
				break;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchMenuItem = menu.findItem(R.id.search);
		searchView = (SearchView) searchMenuItem.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		int searchIconId = searchView.getContext().getResources().
				getIdentifier("android:id/search_button", null, null);
		ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
		searchIcon.setImageResource(R.drawable.ic_search);

		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);

		// Handle action buttons
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * try to collapse search view on actionbar, doesn't work.
		 */
//		searchMenuItem.collapseActionView();
//		searchView.setQuery("", false);
	}
}
