package com.gpfduoduo.wechat.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gpfduoduo.fragmentutil.ui.BaseActivity;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.contact.ContactFragment;
import com.gpfduoduo.wechat.ui.fragment.discovery.DiscoveryFragment;
import com.gpfduoduo.wechat.ui.fragment.me.MeFragment;
import com.gpfduoduo.wechat.ui.fragment.weichat.WeChatFragment;
import com.gpfduoduo.wechat.util.DeviceUtil;

public class MainActivity extends BaseActivity {

    public static final String tag = MainActivity.class.getSimpleName();
    private static final String TAB_POS = "tab_pos";
    private static final String TAB_OPEN = "tab_open";

    private ImageView[] mTabButtons;
    private TextView[] mTabTextViews;
    private LinearLayout mBottomTab;
    private BaseFragment[] mFragments = new BaseFragment[4];
    private int mIndex;
    private int mCurrentTabIndex = 0;// 当前fragment的index
    private boolean mIsTabOpen = true;
    private ObjectAnimator mTransAnimator;
    private LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private int mWidth;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mFragments[0] = WeChatFragment.newInstance();
            mFragments[1] = ContactFragment.newInstance();
            mFragments[2] = DiscoveryFragment.newInstance();
            mFragments[3] = MeFragment.newInstance();

            loadMultipleFragments(R.id.fragment_container, 0, mFragments[0],
                    mFragments[1], mFragments[2], mFragments[3]);
        }
        else {
            mFragments[0] = findFragment(WeChatFragment.class);
            mFragments[1] = findFragment(ContactFragment.class);
            mFragments[2] = findFragment(DiscoveryFragment.class);
            mFragments[3] = findFragment(MeFragment.class);

            mCurrentTabIndex = savedInstanceState.getInt(TAB_POS);
        }

        mWidth = DeviceUtil.getScreenWidth(getApplicationContext());
        initTabView();
    }


    private void initTabView() {
        mBottomTab = (LinearLayout) findViewById(R.id.main_bottom_include);

        mTabButtons = new ImageView[4];
        mTabButtons[0] = (ImageView) mBottomTab.findViewById(R.id.ib_weixin);
        mTabButtons[1] = (ImageView) mBottomTab.findViewById(
                R.id.ib_contact_list);
        mTabButtons[2] = (ImageView) mBottomTab.findViewById(R.id.ib_find);
        mTabButtons[3] = (ImageView) mBottomTab.findViewById(R.id.ib_profile);

        mTabButtons[mCurrentTabIndex].setSelected(true);

        mTabTextViews = new TextView[4];
        mTabTextViews[0] = (TextView) mBottomTab.findViewById(R.id.tv_weixin);
        mTabTextViews[1] = (TextView) mBottomTab.findViewById(
                R.id.tv_contact_list);
        mTabTextViews[2] = (TextView) mBottomTab.findViewById(R.id.tv_find);
        mTabTextViews[3] = (TextView) mBottomTab.findViewById(R.id.tv_profile);

        mTabTextViews[mCurrentTabIndex].setTextColor(0xFF45C01A);
    }


    public void onTabClicked(View view) {

        switch (view.getId()) {
            case R.id.re_weixin:
                mIndex = 0;
                break;
            case R.id.re_contact_list:
                mIndex = 1;
                break;
            case R.id.re_find:
                mIndex = 2;
                break;
            case R.id.re_profile:
                mIndex = 3;
                break;
        }

        mTabButtons[mCurrentTabIndex].setSelected(false);
        mTabButtons[mIndex].setSelected(true);
        //魔鬼数据，暂时不要关心
        mTabTextViews[mCurrentTabIndex].setTextColor(0xFF999999);
        mTabTextViews[mIndex].setTextColor(0xFF45C01A);
        showHideFragment(mFragments[mIndex], mFragments[mCurrentTabIndex]);
        mCurrentTabIndex = mIndex;
    }


    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POS, mCurrentTabIndex);
        outState.putBoolean(TAB_OPEN, mIsTabOpen);
    }


    /**
     * 对子最底下的tab进行打开和关闭的动画
     */
    public void openBottomTab(long duration, boolean open) {
        mIsTabOpen = open;
        float start = 0;
        float end = -mWidth;
        if (open) {
            start = -mWidth;
            end = 0;
        }
        mTransAnimator = ObjectAnimator.ofFloat(mBottomTab, "translationX",
                start, end);
        mTransAnimator.setDuration(duration);
        mTransAnimator.setInterpolator(mLinearInterpolator);
        mTransAnimator.start();
    }


    /**
     * SwipeBack返回时的处理
     */
    public void SwipeOpenBottomTab(float percent) {
        float value = (int) ((float) (-mWidth + (float) mWidth * percent));
        if (value >= 0) {
            value = 0;
        }
        mBottomTab.setX(value);
        mBottomTab.setAlpha(percent);
    }
}

