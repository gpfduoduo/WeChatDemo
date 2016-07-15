package com.gpfduoduo.wechat.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.gpfduoduo.fragmentutil.BaseApplication;

/**
 * Created by gpfduoduo on 2016/7/13.
 */
public class BaseVerticalAnimFragment extends BaseBackFragment {

    private BaseApplication mBaseApplication;


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVerticalAnim();
    }


    private void initVerticalAnim() {
        mBaseApplication = BaseApplication.getContext();
        mFragmentAnim = mBaseApplication.getVerticalFragmentAnim();

        mNoAnim = AnimationUtils.loadAnimation(mBaseApplication,
                com.gpfduoduo.fragmentutil.R.anim.no_anim);
        mEnterAnim = AnimationUtils.loadAnimation(mBaseApplication,
                mFragmentAnim.getEnter());
        mExitAnim = AnimationUtils.loadAnimation(mBaseApplication,
                mFragmentAnim.getExit());
        mPopEnterAnim = AnimationUtils.loadAnimation(mBaseApplication,
                mFragmentAnim.getPopEnter());
        mPopExitAnim = AnimationUtils.loadAnimation(mBaseApplication,
                mFragmentAnim.getPopExit());

        mEnterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {

            }


            @Override public void onAnimationEnd(Animation animation) {
                mBaseActivity.setFragmentClickable(true);
            }


            @Override public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
}
