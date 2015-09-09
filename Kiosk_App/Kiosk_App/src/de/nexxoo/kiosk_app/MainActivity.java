package de.nexxoo.kiosk_app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

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

	@Override
	protected void onStart() {
		super.onStart();
		if (!Misc.isOnline(this)) {
			new AlertDialog.Builder(context)
					.setMessage("Please open WiFi or 3G to " +
							"review the content")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
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
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(R.drawable.ic_menu_white_36dp);
		getActionBar().setTitle(Nexxoo.getStyledText(context, "Anleitung"));

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

		// enable ActionBar app icon to behave as action to toggle nav drawer
		actionbar = getActionBar();


		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_title,  /* "open drawer" description for accessibility */
				R.string.drawer_title  /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mFragmentTitles[pager.getCurrentItem()]);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
//				getActionBar().setTitle(mFragmentTitles[pager.getCurrentItem()]);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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

		tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionbar.setTitle(Nexxoo.getStyledText(context, mFragmentTitles[pager
						.getCurrentItem()]));
				pager.getChildAt(position).requestFocus();
			}
		});

		/**
		 * end: inflate content frame in container
		 */
	}

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
				Intent contact = new Intent(context, ContactActivity.class);
				startActivity(contact);
				break;
			case 1:
				Intent imprint = new Intent(context, ImprintActivity.class);
				startActivity(imprint);
				break;
			case 2:
				Intent history = new Intent(context, HistoryActivity.class);
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
		MenuItem searchMI = (MenuItem) menu.findItem(R.id.search);
		searchMI.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem menuItem) {
				//nothing
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem menuItem) {
				getActionBar().setDisplayShowHomeEnabled(false);
				return true;
			}
		});
		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
//		getActionBar().setIcon(R.drawable.ic_chevron_left_white_36dp);
		getActionBar().setDisplayShowHomeEnabled(false);

		// Handle action buttons
		return super.onOptionsItemSelected(item);
	}

}
