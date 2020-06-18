package com.dreamfish.restreminders.utils;

import android.graphics.drawable.Drawable;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class IconAndTextFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments ;
    private List<String> mTitles ;
    private List<Drawable> mIcons ;

    public IconAndTextFragmentAdapter(FragmentManager fm, List<Fragment> fragments,
                                      List<String> titles, List<Drawable> icons) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
        mIcons = icons;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    public Drawable getPageIcon(int position) {
        return mIcons.get(position);
    }
}
