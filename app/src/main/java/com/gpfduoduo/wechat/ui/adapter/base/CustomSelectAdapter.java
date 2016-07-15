package com.gpfduoduo.wechat.ui.adapter.base;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomSelectAdapter<T> extends CustomAdapter<T> {

    protected List<T> mSelectedList;


    public CustomSelectAdapter(Context context, int resLayoutId, List<T> list) {
        super(context, resLayoutId, list);
        mSelectedList = new ArrayList<T>();
    }


    public List<T> getSelectedList() {
        return mSelectedList;
    }
}
