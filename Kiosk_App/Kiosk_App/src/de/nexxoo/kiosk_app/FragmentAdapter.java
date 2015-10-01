package de.nexxoo.kiosk_app;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    private String[] titles = new String[]{"Anleitungen", "Videos", "Kataloge"};
    private List<Fragment> fragmentList;

    public FragmentAdapter(FragmentManager fm,List<Fragment> objects,ActionBar actionbar) {
        super(fm);
        fragmentList = objects;
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
        return 3;
    }

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public int getItemPosition(Object object) {
//		if(object instanceof ManualFragment){
//			ManualFragment manual = (ManualFragment) object;
//		}
		return super.getItemPosition(object);
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
