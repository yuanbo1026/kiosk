package de.nexxoo.kiosk_app;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ManualFragment());
		fragmentList.add(new VideoFragment());
		fragmentList.add(new CatalogFragment());


		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
		pager.setAdapter(fragmentAdapter);

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_protocol:
				Intent protocol = new Intent(this, ProtocolActivity.class);
				startActivity(protocol);
				return true;
			case R.id.action_contact:
				Intent contact = new Intent(this, ContactActivity.class);
				startActivity(contact);
				return true;
			case R.id.action_imprint:
				Intent imprint = new Intent(this, ImprintActivity.class);
				startActivity(imprint);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
