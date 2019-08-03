package com.example.recorder.ui.home;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapger extends FragmentPagerAdapter {
    private String[] titles = {"Record","Saved Records"};

    public PagerAdapger(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new RecordFragment();
            case 1:
                return new FileViewerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
