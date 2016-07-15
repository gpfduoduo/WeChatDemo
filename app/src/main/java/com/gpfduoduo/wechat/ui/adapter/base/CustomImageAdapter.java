package com.gpfduoduo.wechat.ui.adapter.base;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import java.util.List;

public abstract class CustomImageAdapter<T> extends CustomSelectAdapter<T>
        implements OnScrollListener {
    protected boolean mIsScrolling;
    protected AbsListView mListView;

    private OnScrollListener mListener;


    public CustomImageAdapter(AbsListView view, Context context, int resLayoutId, List<T> list) {
        super(context, resLayoutId, list);
        this.mListView = view;
        mListView.setOnScrollListener(this);
    }


    public void addOnScrollListener(OnScrollListener l) {
        this.mListener = l;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mListener != null) {
            mListener.onScrollStateChanged(view, scrollState);
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mListener != null) {
            mListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }
}