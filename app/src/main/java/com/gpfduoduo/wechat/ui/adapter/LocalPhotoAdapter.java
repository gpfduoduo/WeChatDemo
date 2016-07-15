package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.adapter.base.CustomImageAdapter;
import com.gpfduoduo.wechat.ui.adapter.base.ViewHolder;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.imageloader.ImageLoader;
import java.io.File;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/4.
 */
public class LocalPhotoAdapter extends CustomImageAdapter<String>
        implements AbsListView.OnScrollListener {

    private boolean mIsInit = false;
    private int startIndex, endIndex;
    private String mDirPath;
    private StringBuilder mStringBuilder;
    private BaseFragment mBaseFragment;


    public LocalPhotoAdapter(BaseFragment fragment, AbsListView view, Context context, int resLayoutId, List<String> list, String dirPath) {
        super(view, context, resLayoutId, list);
        mDirPath = dirPath + File.separator;
        mStringBuilder = new StringBuilder(mDirPath);
        addOnScrollListener(this);
        mBaseFragment = fragment;
    }


    @Override
    public void convert(ViewHolder viewHolder, String path, int position) {
        ImageView photo = (ImageView) viewHolder.getImageView(
                R.id.imageview_photo);
        photo.setImageDrawable(MyApplication.getContext()
                                            .getResources()
                                            .getDrawable(
                                                    R.drawable.ic_photo_loading));
        ImageView select = (ImageView) viewHolder.getImageView(R.id.checkmark);
        String absolutePath = mStringBuilder.append(path).toString();
        photo.setTag(absolutePath);
        if (mSelectedList.contains(absolutePath)) {
            select.setSelected(true);
        }
        else {
            select.setSelected(false);
        }

        if (!mIsInit) {
            Glide.with(mBaseFragment)
                 .load(absolutePath)
                 .placeholder(R.drawable.ic_photo_loading)
                 .into(photo);
            //ImageLoader.getInstance().loadImage(absolutePath, photo);
        }

        mStringBuilder.setLength(mDirPath.length());
    }


    public void setDirPath(String dirPath) {
        mDirPath = dirPath + File.separator;
        mStringBuilder.setLength(0);
        mStringBuilder = mStringBuilder.append(mDirPath);
        mIsInit = false;
    }


    public void selectPos(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        String path = mStringBuilder.append(name).toString();

        if (mSelectedList.contains(path)) {
            mSelectedList.remove(path);
        }
        else {
            mSelectedList.add(path);
        }
        mIsInit = false;
        mStringBuilder.setLength(mDirPath.length());

        notifyDataSetChanged();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mIsInit = true;
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
                for (; startIndex < endIndex; startIndex++) {
                    String path = mList.get(startIndex);
                    if (TextUtils.isEmpty(path)) {
                        continue;
                    }
                    String absolutePath = mStringBuilder.append(path)
                                                        .toString();
                    ImageView imageview = (ImageView) mListView.findViewWithTag(
                            absolutePath);
                    //Glide.with(mBaseFragment)
                    //     .load(absolutePath)
                    //     .placeholder(R.drawable.ic_photo_loading)
                    //     .into(imageview);
                    ImageLoader.getInstance()
                               .loadImage(absolutePath, imageview);
                    mStringBuilder.setLength(mDirPath.length());
                }
                break;
        }
    }


    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        startIndex = firstVisibleItem;
        endIndex = firstVisibleItem + visibleItemCount;
    }
}
