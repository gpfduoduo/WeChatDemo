package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.view.autogridview.AutoGridAdapter;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class FriendCirclePhotoAdapter extends AutoGridAdapter<String> {

    private static final String tag
            = FriendCirclePhotoAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private Context mContext;


    public FriendCirclePhotoAdapter(Context context, List<String> list) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    @Override public int getCount() {
        return list.size();
    }


    @Override public String getUrl(int position) {
        return "";
    }


    @Override public Object getItem(int position) {
        return list.get(position);
    }


    @Override public long getItemId(int position) {
        return position;
    }


    @Override public View getView(int position, View view) {
        if (view == null) {
            view = mInflater.inflate(
                    R.layout.view_friend_circle_list_photos_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.photo = (ImageView) view.findViewById(
                    R.id.friend_circle_item_photos_item_photo);
            view.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        String path = list.get(position);
        //Log.d(tag, "load path = " + path);
        Glide.with(mContext)
             .load(path)
             .placeholder(R.drawable.pictures_no)
             .into(mViewHolder.photo);
        return view;
    }


    class ViewHolder {
        ImageView photo;
    }
}
