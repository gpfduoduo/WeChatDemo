package com.gpfduoduo.wechat.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.MainActivity;

/**
 * Created by gpfduoduo on 2016/6/27.
 */
public abstract class BaseDelayLoadFragment extends BaseFragment {

    private static final String tag
            = BaseDelayLoadFragment.class.getSimpleName();

    private boolean mIsInit = false;
    private Bundle mSaveInstanceState;


    public abstract void initLazyView(Bundle saveInstanceState);


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSaveInstanceState = savedInstanceState;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mSaveInstanceState == null) {
            if (!isHidden()) {
                mIsInit = true;
                init(null);
            }
        }
        else {
            if (!isSupportHidden()) {
                mIsInit = true;
                init(mSaveInstanceState);
            }
        }
    }


    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!mIsInit && !hidden) {
            mIsInit = true;
            init(mSaveInstanceState);
        }
    }


    public void init(Bundle saveInstanceState) {
        initLazyView(saveInstanceState);
    }


    public boolean onBackPressedSupport() {
        int childCount = getChildFragmentManager().getBackStackEntryCount();
        if (childCount > 1) {
            popChild();
            if (childCount == 2) {
                hideTabBar(View.VISIBLE, true);
            }
        }
        else {
            getActivity().moveTaskToBack(true);
        }

        return true; //必须是return true, BaseDelayFragment消费该事件
    }


    /**
     * 显示隐藏Tab Bar
     */
    public void hideTabBar(int visible, boolean animation) {
        FrameLayout container = (FrameLayout) getActivity().findViewById(
                R.id.fragment_container);
        ViewGroup.MarginLayoutParams params
                = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
        if (visible == View.GONE) {
            params.bottomMargin = 0;
            ((MainActivity) getActivity()).openBottomTab(
                    animation ? mEnterAnim.getDuration() : 0, false);
        }
        else {
            params.bottomMargin = (int) getResources().getDimension(
                    R.dimen.bottombar_height);
            ((MainActivity) getActivity()).openBottomTab(
                    animation ? mEnterAnim.getDuration() : 0, true);
        }
    }


    public void SwipeOpenBottomTab(float percent) {
        ((MainActivity) getActivity()).SwipeOpenBottomTab(percent);
    }
}
