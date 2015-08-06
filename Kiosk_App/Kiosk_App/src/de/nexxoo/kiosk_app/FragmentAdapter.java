package de.nexxoo.kiosk_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Manual", "Video", "Catalog"};
    private List<Fragment> fragmentList;

    public FragmentAdapter(FragmentManager fm,List<Fragment> objects) {
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
}
