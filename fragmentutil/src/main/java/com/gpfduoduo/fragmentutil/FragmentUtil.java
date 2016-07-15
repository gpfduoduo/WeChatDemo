package com.gpfduoduo.fragmentutil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.gpfduoduo.fragmentutil.entity.FragmentStack;
import com.gpfduoduo.fragmentutil.ui.BaseActivity;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/25.
 */
public class FragmentUtil {

    private static final String tag = FragmentUtil.class.getSimpleName();

    public static final String FRAGMENT_SAVE_STATE_HIDDEN
            = "fragment_state_save_hidden";
    public static final String ARG_IS_ROOT = "fragment_is_root";

    private BaseActivity mBaseActivity;


    public FragmentUtil(WeakReference<BaseActivity> activity) {
        this.mBaseActivity = activity.get();
    }


    /**
     * get the activity fragment, which is not hidden
     *
     * @param parentFragment the parent fragment of the activity one
     * @param fragmentManager the fragment manager
     */
    public BaseFragment getActiveFragment(BaseFragment parentFragment, FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return parentFragment;
        }

        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment == null) continue;
            if (fragment instanceof BaseFragment) {
                BaseFragment baseFragment = (BaseFragment) fragment;
                //assure the fragment is not hidden
                if (!fragment.isHidden() && fragment.getUserVisibleHint()) {
                    return getActiveFragment(baseFragment,
                            fragment.getChildFragmentManager());
                }
            }
        }

        return parentFragment;
    }


    /**
     * load multiple fragments
     */
    public void loadMultipleFragments(FragmentManager fragmentManager, int containerId, int showPos, BaseFragment... fragments) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        for (int i = 0; i < fragments.length; i++) {
            BaseFragment fragment = fragments[i];
            if (fragment == null) {
                throw new IllegalArgumentException(
                        "loadMultipleFragments fragment in list can not be null ");
            }
            String tag = fragment.getClass().getName();
            ft.add(containerId, fragment, tag);
            if (showPos != i) {
                ft.hide(fragment);
            }
            Bundle bundle = fragment.getArguments();
            if (bundle != null) {
                bundle.putBoolean(ARG_IS_ROOT, true); //root Fragment 不需要动画
            }
        }
        ft.commit();
    }


    public void loadRootFragment(FragmentManager fragmentManager, int containerId, BaseFragment fragment) {
        Log.d(tag, "load root fragment");
        start(fragmentManager, containerId, null, fragment);
    }


    /**
     * 启动一个Fragment，原来的Fragment hide
     */
    public void start(FragmentManager fragmentManager, int containerId, BaseFragment fromFragment, BaseFragment fragment) {
        mBaseActivity.setFragmentClickable(false);

        String tag = fragment.getClass().getName();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(containerId, fragment, tag);

        if (fromFragment == null) { //root Fragment不需要动画
            Bundle bundle = fragment.getArguments();
            if (bundle != null) {
                bundle.putBoolean(ARG_IS_ROOT, true);
                mBaseActivity.setFragmentClickable(true); //
            }
        }
        else {
            ft.hide(fromFragment);
        }
        ft.addToBackStack(tag);
        ft.commit();
    }


    /**
     * 启动一个新的 Fragment，并将当前Fragment出栈
     */
    public void startAndFinish(FragmentManager fragmentManager, int containerId, BaseFragment fromFragment, BaseFragment toFragment) {
        mBaseActivity.setFragmentClickable(false);
        BaseFragment preFragment = getPreFragment(fromFragment);

        fragmentManager.beginTransaction().remove(fromFragment);
        fragmentManager.popBackStack();

        String tag = toFragment.getClass().getName();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(containerId, toFragment, tag);
        ft.addToBackStack(tag);

        if (preFragment != null) {
            ft.hide(preFragment);
        }
        ft.commit();
    }


    /**
     * 出栈Fragment
     */
    public void back(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
        }
    }


    public void popTo(Class<?> fragmentClass, boolean includeSelf, FragmentManager fragmentManager) {
        Fragment toFragment = fragmentManager.findFragmentByTag(
                fragmentClass.getName());
        if (includeSelf) {
            toFragment = getPreFragment(toFragment);
            if (toFragment == null) {
                throw new RuntimeException(
                        "pop all fragment please, invoke " + "finish");
            }
        }
        int flag = includeSelf ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0;
        fragmentManager.popBackStackImmediate(toFragment.getClass().getName(),
                flag);
    }


    public void showHideFragment(FragmentManager fragmentManager, BaseFragment show, BaseFragment hide) {
        if (show == hide) {
            return;
        }
        fragmentManager.beginTransaction().show(show).hide(hide).commit();
    }


    /**
     * 获取该Fragment的前一个Fragment
     */
    public BaseFragment getPreFragment(Fragment fragment) {
        List<Fragment> fragments = fragment.getFragmentManager().getFragments();
        if (fragments == null) {
            return null;
        }
        int index = fragments.indexOf(fragment);
        for (int i = index - 1; i > 0; i--) {
            Fragment preFragment = fragments.get(i);
            if (preFragment != null && preFragment instanceof BaseFragment) {
                return (BaseFragment) preFragment;
            }
        }
        return null;
    }


    /**
     * 获取FragmentManager的栈顶Fragment
     */
    public BaseFragment getTopFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();

        if (fragments == null) {
            return null;
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment == null) return null;
            if (fragment instanceof BaseFragment) {
                return (BaseFragment) fragment;
            }
        }

        return null;
    }


    public <T extends BaseFragment> T findStackFragment(Class<T> fragmentClass, FragmentManager fragmentManager, boolean isChild) {
        Fragment fragment = null;
        if (isChild) {
            List<Fragment> childFragmentList = fragmentManager.getFragments();
            if (childFragmentList == null) return null;

            for (int i = childFragmentList.size() - 1; i >= 0; i--) {
                Fragment childFragment = childFragmentList.get(i);
                if (childFragment instanceof BaseFragment &&
                        childFragment.getClass()
                                     .getName()
                                     .equals(fragmentClass.getName())) {
                    fragment = childFragment;
                    break;
                }
            }
        }
        else {
            fragment = fragmentManager.findFragmentByTag(
                    fragmentClass.getName());
        }
        if (fragment == null) {
            return null;
        }
        return (T) fragment;
    }


    public List<FragmentStack> getFragmentRecords() {
        List<FragmentStack> fragmentRecords = new ArrayList<>();

        List<Fragment> fragmentList = mBaseActivity.getSupportFragmentManager()
                                                   .getFragments();
        if (fragmentList == null || fragmentList.size() < 1) return null;

        for (Fragment fragment : fragmentList) {
            if (fragment == null) continue;
            fragmentRecords.add(
                    new FragmentStack(fragment.getClass().getSimpleName(),
                            getChildFragmentRecords(fragment)));
        }

        return fragmentRecords;
    }


    private List<FragmentStack> getChildFragmentRecords(Fragment parentFragment) {
        List<FragmentStack> fragmentRecords = new ArrayList<>();

        List<Fragment> fragmentList = parentFragment.getChildFragmentManager()
                                                    .getFragments();
        if (fragmentList == null || fragmentList.size() < 1) return null;

        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment != null) {
                fragmentRecords.add(
                        new FragmentStack(fragment.getClass().getSimpleName(),
                                getChildFragmentRecords(fragment)));
            }
        }
        return fragmentRecords;
    }
}
