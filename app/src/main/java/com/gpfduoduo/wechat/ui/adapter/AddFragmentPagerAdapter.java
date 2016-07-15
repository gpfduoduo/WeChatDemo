package com.gpfduoduo.wechat.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.gpfduoduo.wechat.ui.fragment.weichat.AddItemFragmentOne;
import com.gpfduoduo.wechat.ui.fragment.weichat.AddItemFragmentTwo;

/**
 * Created by gpfduduo on 2016/7/12.
 */
public class AddFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount = 2;


    public AddFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override public Fragment getItem(int position) {
        if (position == 0) {
            return AddItemFragmentOne.newInstance();
        }
        else if (position == 1) {
            return AddItemFragmentTwo.newInstance();
        }
        return null;
    }


    @Override public int getCount() {
        return mCount;
    }
}
