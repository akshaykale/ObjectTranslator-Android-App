package com.akshaykale.objecttranslator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public String[] tabsTitles = {"ObjectTranslator"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        switch (i){

            case 0:
                fragment = new CaptureFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabsTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }
}
