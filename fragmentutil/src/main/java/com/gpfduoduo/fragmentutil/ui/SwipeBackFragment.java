package com.gpfduoduo.fragmentutil.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.fragmentutil.ui.view.SwipeBackLayout;

/**
 * Created by gpfduoduo on 2016/7/25.
 */
public class SwipeBackFragment extends BaseFragment {

    private static final String tag = SwipeBackFragment.class.getSimpleName();

    protected SwipeBackLayout mSwipeBackLayout;


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSwipeBackLayout = new SwipeBackLayout(mBaseActivity);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
        mSwipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
    }


    //必须调用
    protected View attachToSwipeBack(View view) {
        mSwipeBackLayout.attachToFragment(this, view);
        return mSwipeBackLayout;
    }


    protected Fragment getPreFragment() {
        return mSwipeBackLayout.getPreFragment();
    }
}
