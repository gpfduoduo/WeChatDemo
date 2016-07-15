package com.gpfduoduo.wechat.ui.view.autogridview;


import java.util.List;

import android.content.Context;
import android.view.View;


public abstract class AutoGridAdapter<T>
{
    protected Context context;
    protected List<T> list;

    public AutoGridAdapter(Context context, List<T> list)
    {
        this.context = context;
        this.list = list;
    }

    public abstract int getCount();

    public abstract String getUrl(int position);

    public abstract Object getItem(int position);

    public abstract long getItemId(int position);

    public abstract View getView(int position, View view);
}
