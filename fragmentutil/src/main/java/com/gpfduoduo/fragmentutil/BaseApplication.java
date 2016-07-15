package com.gpfduoduo.fragmentutil;

import android.app.Application;
import com.gpfduoduo.fragmentutil.anim.DefaultHorizontalAnimator;
import com.gpfduoduo.fragmentutil.anim.FragmentAnim;

/**
 * Created by gpfduoduo on 2016/6/25.
 */
public class BaseApplication extends Application {

    protected static BaseApplication sContext;

    protected FragmentAnim mFragmentAnim, mVerFragmentAnim;


    @Override public void onCreate() {
        super.onCreate();

        sContext = this;

        initAnim();
    }


    private void initAnim() {
        mFragmentAnim = new DefaultHorizontalAnimator();
    }


    public FragmentAnim getFragmentAnim() {
        return mFragmentAnim;
    }


    public FragmentAnim getVerticalFragmentAnim() {
        return mVerFragmentAnim;
    }


    public void setVerticalFragmentAnim(FragmentAnim anim) {
        mVerFragmentAnim = anim;
    }


    public static BaseApplication getContext() {
        return sContext;
    }
}
