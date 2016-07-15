package com.gpfduoduo.fragmentutil.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import com.gpfduoduo.fragmentutil.BaseApplication;
import com.gpfduoduo.fragmentutil.FragmentUtil;
import com.gpfduoduo.fragmentutil.R;
import com.gpfduoduo.fragmentutil.anim.FragmentAnim;

/**
 * Created by gpfduoduo on 2016/6/25.
 */
public class BaseFragment extends Fragment {

    private static final String tag = BaseFragment.class.getSimpleName();

    private BaseApplication mBaseApplication;
    protected FragmentAnim mFragmentAnim;

    protected Animation mNoAnim, mEnterAnim, mExitAnim, mPopEnterAnim,
            mPopExitAnim;

    protected BaseActivity mBaseActivity;
    private FragmentUtil mFragmentUtil;
    private boolean mIsHidden = true;
    private boolean mIsRoot = false;
    private InputMethodManager mInputManager;


    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBaseActivity = (BaseActivity) activity;
        mFragmentUtil = mBaseActivity.getFragmentUtil();
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {//用来判断是否为根root，根root是没有进入动画的
            mIsRoot = bundle.getBoolean(FragmentUtil.ARG_IS_ROOT, false);
        }

        if (savedInstanceState != null) {
            mIsHidden = savedInstanceState.getBoolean(
                    FragmentUtil.FRAGMENT_SAVE_STATE_HIDDEN);
            processSaveState();
        }

        initAnim();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //以Fragment A 进入 Fragment B 为例
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if (enter) { //A进入时的动画
                if (mIsRoot) { // root fragment 没有进入动画
                    return mNoAnim;
                }
                return mEnterAnim;
            }
            else { //B进入时A Hide的动画
                return mPopExitAnim;
            }
        }
        else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
            if (enter) {  //B finish 时 A进入的动画
                return mPopEnterAnim;
            }
            else { //B finish时的动画
                return mExitAnim;
            }
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FragmentUtil.FRAGMENT_SAVE_STATE_HIDDEN,
                isHidden());
    }


    /**
     * true则消费掉该事件, false 返回上一个页面
     */
    public boolean onBackPressedSupport() {
        return false;
    }


    public boolean isSupportHidden() {
        return mIsHidden;
    }


    private void initAnim() {
        mBaseApplication = BaseApplication.getContext();
        mFragmentAnim = mBaseApplication.getFragmentAnim();

        mNoAnim = AnimationUtils.loadAnimation(mBaseApplication,
                R.anim.no_anim);
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


    public long getPopEnterAnimDuration() {
        return mPopEnterAnim.getDuration();
    }


    public long getPopExitAnimDuration() {
        return mPopExitAnim.getDuration();
    }


    /**
     * 将Fragment出栈
     */
    public void pop() {
        mFragmentUtil.back(getFragmentManager());
    }


    /**
     * 将Fragment作为容易的子Fragments进行出栈
     */
    public void popChild() {
        mFragmentUtil.back(this.getChildFragmentManager());
    }


    /**
     * 出栈到某一个Fragment, includeSelf是否包含自己？
     * 使用场景：FragmentA进入FragmentB进入FragmentC，最后由FragmentC进行FragmentA
     */
    public void popTo(Class<?> fragmentClass, boolean includeSelf) {
        mFragmentUtil.popTo(fragmentClass, includeSelf, getFragmentManager());
    }


    /**
     * 启动一个Fragment将原来的Fragment hide掉
     */
    public void start(int containerId, BaseFragment fragment) {
        mFragmentUtil.start(getFragmentManager(), containerId, this, fragment);
    }


    /**
     * 在Fragment中加载根Fragment
     */
    public void loadRootFragment(int containerId, BaseFragment fragment) {
        mFragmentUtil.loadRootFragment(getChildFragmentManager(), containerId,
                fragment);
    }


    /**
     * 启动一个新的Fragment，并把原来的出栈
     */
    public void startAndFinish(int containerId, BaseFragment fragment) {
        mFragmentUtil.startAndFinish(getFragmentManager(), containerId, this,
                fragment);
    }


    protected void showInput(final View view) {
        if (view == null) return;
        initInputManager();
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override public void run() {
                mInputManager.showSoftInput(view,
                        InputMethodManager.SHOW_FORCED);
            }
        }, 200);
    }


    protected void hideInput(View view) {
        if (view == null) {
            return;
        }
        initInputManager();
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void initInputManager() {
        if (mInputManager == null) {
            mInputManager = (InputMethodManager) mBaseActivity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
        }
    }


    private void processSaveState() {
        //内存重启后，根据Fragment的onSaveInstanceState中保存的确定过来确定是否已显示Fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mIsHidden) {
            ft.hide(this);
        }
        else {
            ft.show(this);
        }
        ft.commit();
    }
}
