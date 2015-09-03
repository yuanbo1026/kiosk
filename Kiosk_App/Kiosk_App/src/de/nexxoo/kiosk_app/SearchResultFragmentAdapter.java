package de.nexxoo.kiosk_app;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;

public class SearchResultFragmentAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    private String[] titles = new String[]{"Alle","Anleitung", "Video", "Katalog"};
    private List<Fragment> fragmentList;
	private ActionBar mActionBar;

    public SearchResultFragmentAdapter(FragmentManager fm, List<Fragment> objects, ActionBar actionbar) {
        super(fm);
        fragmentList = objects;
		mActionBar = actionbar;
    }

    @Override
    public Fragment getItem(int i) {

        return fragmentList.get(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 4;
    }

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {
//		mActionBar.setSelectedNavigationItem(i);
////		int resIdLenght = resId.length;
////		if (position < 0 || position >= resIdLenght)
////			return;
////		int drawableId = resId[position];
//		mActionBar.setTitle(titles[i]);
	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}
}
