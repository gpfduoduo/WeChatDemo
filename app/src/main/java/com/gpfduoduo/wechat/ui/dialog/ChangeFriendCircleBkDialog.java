package com.gpfduoduo.wechat.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;

/**
 * Created by Administrator on 2016/7/17.
 */
public class ChangeFriendCircleBkDialog extends PopupDialog {

    private View.OnClickListener onItemClickListener;


    public ChangeFriendCircleBkDialog(Context context) {
        super(context);
    }


    public ChangeFriendCircleBkDialog(Context context, View.OnClickListener listener) {
        super(context);
        super.setContentView(R.layout.view_friend_cicle_change_bk_dialog);
        onItemClickListener = listener;
        init();
    }


    private void init() {
        ((TextView) findViewById(
                R.id.friend_circle_item_change_bk)).setOnClickListener(
                onItemClickListener);
        ((TextView) findViewById(
                R.id.friend_circle_item_cancel)).setOnClickListener(
                onItemClickListener);
    }
}
