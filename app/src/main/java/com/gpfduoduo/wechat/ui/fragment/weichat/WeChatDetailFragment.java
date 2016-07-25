package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.adapter.AddFragmentPagerAdapter;
import com.gpfduoduo.wechat.ui.fragment.BaseSwipeBackFragment;
import com.gpfduoduo.wechat.util.DeviceUtil;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class WeChatDetailFragment extends BaseSwipeBackFragment
        implements View.OnClickListener {

    private static final String TITLE = "wechat_detail_title";
    private String mTitle;
    private ListView mListView;
    private EditText mInputEdit;
    private ImageView mVoiceView, mAddView, mFaceView;
    private ViewPager mAddViewpager;

    private ValueAnimator mTransAnimator;
    private LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private ViewGroup.MarginLayoutParams mMarginLayoutParams;


    public static WeChatDetailFragment newInstance(String name) {
        Bundle bundle = new Bundle();
        WeChatDetailFragment fragment = new WeChatDetailFragment();
        bundle.putString(TITLE, name);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE);
        }
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wechat_list_detail,
                container, false);

        initToolbar(view);
        initListView(view);
        initInputView(view);

        return attachToSwipeBack(view);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_add_button:
                mInputEdit.setFocusable(false);
                showViewPager(true);
                break;
            case R.id.wechat_voice:
                mInputEdit.setFocusable(false);
                break;
            case R.id.wechat_face_button:
                break;
        }
    }


    @Override public void onPause() {
        super.onPause();
        hideInput(mInputEdit);
    }


    private void initInputView(View view) {
        mInputEdit = (EditText) view.findViewById(R.id.wechat_input_edit);
        hideInput(mInputEdit);
        mInputEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    if (hasFocus) { //EditText获取到了焦点
                        showInput(mInputEdit);
                        showViewPager(false);
                    }
                    else { //EditText失去了焦点
                        hideInput(mInputEdit);
                    }
                }
            }
        });
        mInputEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (!mInputEdit.isFocusable()) {
                    mInputEdit.setFocusableInTouchMode(true);
                    mInputEdit.setFocusable(true);
                    if (!mInputEdit.isFocused()) {
                        mInputEdit.requestFocus();
                    }
                }
                return false;
            }
        });

        mAddView = (ImageView) view.findViewById(R.id.wechat_add_button);
        mAddView.setOnClickListener(this);
        mVoiceView = (ImageView) view.findViewById(R.id.wechat_voice);
        mAddView.setOnClickListener(this);
        mFaceView = (ImageView) view.findViewById(R.id.wechat_face_button);
        mAddView.setOnClickListener(this);

        mAddViewpager = (ViewPager) view.findViewById(
                R.id.wechat_list_add_viewpager);
        mAddViewpager.setAdapter(
                new AddFragmentPagerAdapter(getChildFragmentManager()));
        mMarginLayoutParams
                = (ViewGroup.MarginLayoutParams) mAddViewpager.getLayoutParams();
        showViewPager(false);
    }


    private void initListView(View view) {
        mListView = (ListView) view.findViewById(R.id.wechat_list_detail_list);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                mInputEdit.setFocusable(false);
                showViewPager(false);
                return false;
            }
        });
    }


    private void initToolbar(View view) {
        Toolbar toolbar = initToolBar(view, R.id.wechat_detail_toolbar_include,
                R.menu.menu_wechat_list_detail, mTitle,
                getString(R.string.chat));
        initToolbarNav(toolbar);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_wechat_list_detail_friend:
                                logFragmentStack();
                                break;
                        }
                        return true;
                    }
                });
    }


    private void showViewPager(final boolean show) {
        if (mTransAnimator != null && mTransAnimator.isRunning()) { //防止多次执行
            return;
        }
        float height = -DeviceUtil.dip2px(mBaseActivity,
                getResources().getDimension(R.dimen.y180));
        float end = height;
        if (show) {
            end = 0;
        }
        mTransAnimator = ValueAnimator.ofFloat(mMarginLayoutParams.bottomMargin,
                end);
        mTransAnimator.setTarget(mAddViewpager);
        mTransAnimator.setDuration(300);
        mTransAnimator.setInterpolator(mLinearInterpolator);
        mTransAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (float) valueAnimator.getAnimatedValue();
                        mMarginLayoutParams.bottomMargin = (int) value;
                        mAddViewpager.setLayoutParams(mMarginLayoutParams);
                    }
                });
        mTransAnimator.start();
    }
}
