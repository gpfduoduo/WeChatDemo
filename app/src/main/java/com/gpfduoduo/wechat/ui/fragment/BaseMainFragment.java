package com.gpfduoduo.wechat.ui.fragment;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.MainActivity;

/**
 * Created by Administrator on 2016/6/29.
 */
public class BaseMainFragment extends BaseFragment {

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


    protected void hideTabBar() {
        ((BaseDelayLoadFragment) getParentFragment()).hideTabBar(View.GONE,
                true); //
    }


    /**
     * 发现和我页面具有相同更多布局
     */
    protected void setItem(View view, int itemResId, int itemIconResId, int stingResId, View.OnClickListener listener) {
        if (itemResId <= 0) {
            return;
        }

        RelativeLayout item = ((RelativeLayout) view.findViewById(itemResId));
        item.setOnClickListener(listener);
        if (itemIconResId > 0) {
            ((ImageView) item.findViewById(
                    R.id.discovery_me_item_img_icon)).setBackgroundDrawable(
                    getResources().getDrawable(itemIconResId));
        }
        if (stingResId > 0) {
            ((TextView) item.findViewById(
                    R.id.discovery_me_item_textview)).setText(stingResId);
        }
    }
}
