package com.gpfduoduo.fragmentutil.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import com.gpfduoduo.fragmentutil.FragmentUtil;
import com.gpfduoduo.fragmentutil.entity.FragmentStack;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/25.
 * the base class of Activity
 */
public class BaseActivity extends AppCompatActivity {

    private static final String tag = BaseActivity.class.getSimpleName();

    private FragmentUtil mFragmentUtil;
    private boolean mIsFragmentClickable = true;


    public FragmentUtil getFragmentUtil() {
        if (mFragmentUtil == null) {
            mFragmentUtil = new FragmentUtil(
                    new WeakReference<BaseActivity>(this));
        }
        return mFragmentUtil;
    }


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mFragmentUtil == null) {
            mFragmentUtil = new FragmentUtil(
                    new WeakReference<BaseActivity>(this));
        }
    }


    @Override public void onBackPressed() {
        if (!mIsFragmentClickable) {
            setFragmentClickable(true);
        }

        BaseFragment frontFragment = mFragmentUtil.getActiveFragment(null,
                getSupportFragmentManager());

        Log.d(tag, "onBackPressed fragment = " + frontFragment.getTag());

        if (dispatchBackPressedEvent(frontFragment)) {
            return;
        }
        handleBackPressed();
    }


    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsFragmentClickable) { //防止多次点击启动同一个Fragment
            Log.d(tag, "can not click many times");
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }


    /**
     * 在Activity中一次加载多个Fragment（类似于微信主页面的实现）
     */
    public void loadMultipleFragments(int containerId, int showPos, BaseFragment... fragments) {
        if (containerId < 0 || fragments == null) {
            throw new IllegalArgumentException(
                    "loadMultipleFragments function params error");
        }

        mFragmentUtil.loadMultipleFragments(getSupportFragmentManager(),
                containerId, showPos, fragments);
    }


    /**
     * 在Activity中加载根Fragment
     */
    public void loadRootFragment(int containerId, BaseFragment fragment) {
        mFragmentUtil.loadRootFragment(getSupportFragmentManager(), containerId,
                fragment);
    }


    /**
     * 在Activity中启动Fragment
     */
    public void start(int containerId, BaseFragment fragment) {
        mFragmentUtil.start(getSupportFragmentManager(), containerId,
                getTopFragment(), fragment);
    }


    /**
     * 分发回退事件, 优先栈顶(有子栈则是子栈的栈顶)的Fragment
     */
    public boolean dispatchBackPressedEvent(BaseFragment fragment) {
        if (fragment != null) {
            boolean result = fragment.onBackPressedSupport();
            if (result) {
                return true;
            }

            Fragment parentFragment = fragment.getParentFragment();
            if (dispatchBackPressedEvent((BaseFragment) parentFragment)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 显示一个Fragment show 隐藏Fragment hide， 像微信一样
     */
    public void showHideFragment(BaseFragment show, BaseFragment hide) {
        mFragmentUtil.showHideFragment(getSupportFragmentManager(), show, hide);
    }


    /**
     * 获取当前Fragment栈中的第一个Fragment
     */
    public BaseFragment getTopFragment() {
        return mFragmentUtil.getTopFragment(getSupportFragmentManager());
    }


    public <T extends BaseFragment> T findFragment(Class<T> fragmentClass) {
        return mFragmentUtil.findStackFragment(fragmentClass,
                getSupportFragmentManager(), false);
    }


    public void setFragmentClickable(boolean clickable) {
        mIsFragmentClickable = clickable;
    }


    private void handleBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            finish();
        }
    }


    /**
     * 显示栈视图 日志 ,调试时使用
     */
    public void logFragmentStackHierarchy(String TAG) {
        List<FragmentStack> fragmentRecordList
                = mFragmentUtil.getFragmentRecords();

        if (fragmentRecordList == null) return;

        StringBuilder sb = new StringBuilder();

        for (int i = fragmentRecordList.size() - 1; i >= 0; i--) {
            FragmentStack fragmentRecord = fragmentRecordList.get(i);

            if (i == fragmentRecordList.size() - 1) { //栈顶
                sb.append(
                        "═══════════════════════════════════════════════════════════════════════════════════\n");
                if (i == 0) {//仅仅有一个Fragment
                    sb.append(
                            "\t栈顶\t\t\t" + fragmentRecord.fragmentName + "\n");
                    sb.append(
                            "═══════════════════════════════════════════════════════════════════════════════════");
                }
                else { //有多个Fragment
                    sb.append("\t栈顶\t\t\t" + fragmentRecord.fragmentName +
                            "\n\n");
                }
            }
            else if (i == 0) { //栈底
                sb.append("\t栈底\t\t\t" + fragmentRecord.fragmentName + "\n\n");
                processChildLog(fragmentRecord.childFragmentStack, sb, 1);
                sb.append(
                        "═══════════════════════════════════════════════════════════════════════════════════");
                Log.i(TAG, sb.toString());
                return;
            }
            else {
                sb.append("\t↓\t\t\t" + fragmentRecord.fragmentName + "\n\n");
            }

            processChildLog(fragmentRecord.childFragmentStack, sb, 1);
        }
    }


    private void processChildLog(List<FragmentStack> fragmentRecordList, StringBuilder sb, int childHierarchy) {
        if (fragmentRecordList == null || fragmentRecordList.size() == 0) {
            return;
        }

        for (int j = 0; j < fragmentRecordList.size(); j++) {
            FragmentStack childFragmentRecord = fragmentRecordList.get(j);
            for (int k = 0; k < childHierarchy; k++) {
                sb.append("\t\t\t");
            }
            if (j == 0) {
                sb.append("\t子栈顶\t\t" + childFragmentRecord.fragmentName +
                        "\n\n");
            }
            else if (j == fragmentRecordList.size() - 1) {
                sb.append("\t子栈底\t\t" + childFragmentRecord.fragmentName +
                        "\n\n");
                processChildLog(childFragmentRecord.childFragmentStack, sb,
                        ++childHierarchy);
                return;
            }
            else {
                sb.append("\t↓\t\t\t" + childFragmentRecord.fragmentName +
                        "\n\n");
            }

            processChildLog(childFragmentRecord.childFragmentStack, sb,
                    childHierarchy);
        }
    }
}
