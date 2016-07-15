package com.gpfduoduo.wechat.ui.adapter.base;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gpfduoduo.wechat.ui.view.autogridview.AutoGridLayout;

public class ViewHolder {
    SparseArray<View> mViews;
    View mConvertView;


    public ViewHolder(LayoutInflater layoutInflater, int resId, ViewGroup parent) {
        mViews = new SparseArray<View>();
        mConvertView = layoutInflater.inflate(resId, parent, false);
        mConvertView.setTag(this);
    }


    public static ViewHolder getViewHolder(View convertView, ViewGroup parent, int resId, LayoutInflater layoutInflater) {
        if (convertView == null) {
            return new ViewHolder(layoutInflater, resId, parent);
        }
        return (ViewHolder) convertView.getTag();
    }


    public View getConvertView() {
        return mConvertView;
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public TextView getTextView(int viewId) {
        return getView(viewId);
    }


    public ImageView getImageView(int viewId) {
        return getView(viewId);
    }


    public AutoGridLayout getAutoGridLayout(int viewId) {
        return getView(viewId);
    }


    public CheckBox getCheckBox(int viewId) {
        return getView(viewId);
    }


    public RelativeLayout getRelativeLayout(int phoneDirSelectLay) {
        return getView(phoneDirSelectLay);
    }


    public ProgressBar getProgressBar(int linearProgress) {
        return getView(linearProgress);
    }


    public LinearLayout getLinearLayout(int viewId) {
        return getView(viewId);
    }
}
