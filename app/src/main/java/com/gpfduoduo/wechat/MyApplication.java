package com.gpfduoduo.wechat;

import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.gpfduoduo.wechat.ui.MainActivity;
import com.gpfduoduo.fragmentutil.BaseApplication;
import com.gpfduoduo.fragmentutil.anim.DefaultVerticalAnimator;
import com.gpfduoduo.fragmentutil.anim.FragmentAnim;

public class MyApplication extends BaseApplication {

    private FragmentAnim mVerFragmentAnim;


    @Override public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
        initVerticalAnim();
    }


    private void initVerticalAnim() {
        mVerFragmentAnim = new DefaultVerticalAnimator();
        sContext.setVerticalFragmentAnim(mVerFragmentAnim);
    }


    @Override public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(MainActivity.tag, "level = " + level);
        Glide.with(this).onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                System.gc();
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
                break;
            case TRIM_MEMORY_RUNNING_LOW:
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                break;
            /**
             * 当应用程序是缓存的会收到下面的消息
             */
            case TRIM_MEMORY_BACKGROUND:
            case TRIM_MEMORY_MODERATE:
            case TRIM_MEMORY_COMPLETE:
                System.gc();
                break;
        }
    }
}