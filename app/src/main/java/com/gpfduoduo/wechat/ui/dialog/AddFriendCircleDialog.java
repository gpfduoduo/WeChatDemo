package com.gpfduoduo.wechat.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;

/**
 * Created by Administrator on 2016/7/3.
 */
public class AddFriendCircleDialog extends PopupDialog {

    private View.OnClickListener OnItemClickListener;


    public AddFriendCircleDialog(Context context, View.OnClickListener itemClickListener) {
        super(context);
        super.setContentView(R.layout.view_add_friend_cicle_dialog);
        OnItemClickListener = itemClickListener;
        init();
    }


    private void init() {
        ((TextView) findViewById(
                R.id.friend_circle_item_cancel)).setOnClickListener(
                OnItemClickListener);
        ((TextView) findViewById(
                R.id.friend_circle_item_select_from_photo)).setOnClickListener(
                OnItemClickListener);
        ((TextView) findViewById(
                R.id.friend_circle_item_take_photo)).setOnClickListener(
                OnItemClickListener);
        ((TextView) findViewById(
                R.id.friend_circle_item_little_video)).setOnClickListener(
                OnItemClickListener);
    }
}
