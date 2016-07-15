package com.gpfduoduo.fragmentutil.entity;

import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/27.
 */
public class FragmentStack {
    public String fragmentName;
    public List<FragmentStack> childFragmentStack;


    public FragmentStack(String fragmentName, List<FragmentStack> childFragmentRecord) {
        this.fragmentName = fragmentName;
        this.childFragmentStack = childFragmentRecord;
    }


    @Override public String toString() {
        return "fragmentName" + fragmentName + "r\rn" + "child fragment: " +
                getChildFragment(childFragmentStack) + "\r\n";
    }


    private String getChildFragment(List<FragmentStack> childFragmentStack) {
        String result = "";
        if (childFragmentStack != null && childFragmentStack.size() > 0) {
            for (FragmentStack fragmentStack : childFragmentStack) {
                result = result + fragmentStack.fragmentName + "; " +
                        getChildFragment(fragmentStack.childFragmentStack);
            }
        }
        return result;
    }
}
