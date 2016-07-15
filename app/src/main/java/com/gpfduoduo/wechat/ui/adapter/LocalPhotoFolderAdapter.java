package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.adapter.base.CustomAdapter;
import com.gpfduoduo.wechat.ui.adapter.base.ViewHolder;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import com.gpfduoduo.imageloader.ImageFolder;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/5.
 */
public class LocalPhotoFolderAdapter extends CustomAdapter<ImageFolder> {

    private String mSelectedPath;
    private BaseFragment mBaseFragment;


    public LocalPhotoFolderAdapter(BaseFragment fragment, Context context, int resLayoutId, List<ImageFolder> list) {
        super(context, resLayoutId, list);
        mBaseFragment = fragment;
    }


    @Override
    public void convert(ViewHolder viewHolder, ImageFolder imageFolder, int position) {
        TextView dirName = (TextView) viewHolder.getTextView(
                R.id.id_dir_item_name);
        TextView count = (TextView) viewHolder.getTextView(
                R.id.id_dir_item_count);
        ImageView album = (ImageView) viewHolder.getImageView(
                R.id.id_dir_item_image);
        ImageView select = (ImageView) viewHolder.getImageView(
                R.id.id_dir_item_select);

        count.setText("" + imageFolder.getCount());
        dirName.setText(imageFolder.getName());

        String path = imageFolder.getFirstImagePath();
        Glide.with(mBaseFragment)
             .load(path)
             .placeholder(R.drawable.pic_dir)
             .error(R.drawable.pictures_no)
             .into(album);

        //ImageLoader.getInstance().loadImage(path, album);
        if (mSelectedPath != null) {
            if (mSelectedPath.equals(imageFolder.getDir())) {
                select.setVisibility(View.VISIBLE);
            }
            else {
                select.setVisibility(View.GONE);
            }
        }
        else {
            select.setVisibility(View.GONE);
        }
    }


    public void setSelected(String path) {
        mSelectedPath = path;
        this.notifyDataSetChanged();
    }
}
