package com.gpfduoduo.wechat.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.gpfduoduo.fragmentutil.ui.SwipeBackFragment;
import com.gpfduoduo.fragmentutil.ui.view.SwipeBackLayout;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.MainActivity;

/**
 * Created by Administrator on 2016/7/25.
 */
public class BaseSwipeBackFragment extends SwipeBackFragment
        implements SwipeBackLayout.OnSwipeListener {

    private static final String tag
            = BaseSwipeBackFragment.class.getSimpleName();


    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mBaseActivity.onBackPressed();
            }
        });
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册边缘返回监听接口
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.addSwipeListener(this);
        }
    }


    /**
     * STATE_IDLE 0
     * STATE_DRAGGING 1
     * STATE_SETTLING 2
     */
    @Override public void onDragStateChange(int state) {
    }


    @Override public void onEdgeTouch(int orientationEdgeFlag) {
    }


    @Override public void onDragScrolled(float scrollPercent) {
        Fragment preFragment = getPreFragment();
        if (preFragment != null && preFragment instanceof BaseMainFragment) {
            BaseDelayLoadFragment rootFragment
                    = ((BaseDelayLoadFragment) preFragment.getParentFragment());
            if (rootFragment != null) {
                rootFragment.SwipeOpenBottomTab(scrollPercent);
            }
        }
    }


    protected Toolbar initToolBar(View view, int viewStubId, int menuId, String title, String leftTitle) {
        if (view == null || viewStubId < 0) {
            throw new IllegalArgumentException(
                    "toolbar view and view id can not be " + "null");
        }

        ViewStub viewStub = (ViewStub) view.findViewById(viewStubId);
        FrameLayout toolbarView = (FrameLayout) viewStub.inflate();

        Toolbar mToolbar = (Toolbar) toolbarView.findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) toolbarView.findViewById(R.id.toolbar_title)).setText(
                    title);
        }
        if (!TextUtils.isEmpty(leftTitle)) {
            mToolbar.setTitle((CharSequence) leftTitle);
        }

        if (menuId > 0) {
            mToolbar.inflateMenu(menuId);
        }
        return mToolbar;
    }


    @Override public void onResume() {
        super.onResume();
        logFragmentStack();
    }


    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(MainActivity.tag, "setUserVisibleHint = " + isVisibleToUser);
    }


    protected void logFragmentStack() {
        ((MainActivity) getActivity()).logFragmentStackHierarchy(
                MainActivity.tag);
    }
}
