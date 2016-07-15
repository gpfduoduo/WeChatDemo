package com.gpfduoduo.wechat.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * 通用的adapter，继承此adapter，实现convert接口即可。
 */
public abstract class CustomAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mList;
    protected int mResLayoutId;
    protected LayoutInflater mLayoutInflater;


    public CustomAdapter(Context context, int resLayoutId, List<T> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = list;
        this.mResLayoutId = resLayoutId;
    }


    @Override public int getCount() {
        return mList.size();
    }


    @Override public Object getItem(int i) {
        if (i < 0) return mList.get(0);
        return mList.get(i);
    }


    @Override public long getItemId(int i) {
        return i;
    }


    @Override public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(view, viewGroup,
                mResLayoutId, mLayoutInflater);
        if (mList == null || mList.size() <= 0) {
            return viewHolder.getConvertView();
        }
        convert(viewHolder, mList.get(i), i);

        return viewHolder.getConvertView();
    }


    public abstract void convert(ViewHolder viewHolder, T t, int position);
}
