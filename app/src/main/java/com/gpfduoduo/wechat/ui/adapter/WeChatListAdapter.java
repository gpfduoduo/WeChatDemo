package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.Friend;
import com.gpfduoduo.wechat.ui.adapter.base.CustomAdapter;
import com.gpfduoduo.wechat.ui.adapter.base.ViewHolder;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/27.
 */
public class WeChatListAdapter extends CustomAdapter<Friend> {

    public WeChatListAdapter(Context context, int resId, List<Friend> list) {
        super(context, resId, list);
    }


    @Override
    public void convert(ViewHolder viewHolder, Friend friend, int position) {
        TextView name = (TextView) viewHolder.getTextView(
                R.id.wechat_list_friend_name);
        TextView content = (TextView) viewHolder.getTextView(
                R.id.wechat_list_content);

        name.setText(friend.nickName);
        content.setText(friend.lastContent);
    }
}
