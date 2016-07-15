package com.gpfduoduo.wechat.ui.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.util.DeviceUtil;

/**
 * Created by gpfduoduo on 2016/7/8.
 */
public class FriendCircleLovePopupWindow extends PopupWindow
        implements View.OnClickListener {

    private TextView digBtn;
    private TextView commentBtn;

    private final int[] mLocation = new int[2];
    private Rect mRect = new Rect();

    private OnItemClickListener mOnItemClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(int position);
    }


    public FriendCircleLovePopupWindow(Context context) {
        super(context);
        init(context);
    }


    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context)
                                  .inflate(
                                          R.layout.view_friend_circle_love_popwindow,
                                          null);
        digBtn = (TextView) view.findViewById(R.id.digBtn);
        commentBtn = (TextView) view.findViewById(R.id.commentBtn);
        digBtn.setOnClickListener(this);
        commentBtn.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(DeviceUtil.dip2px(context, 100));
        this.setHeight(DeviceUtil.dip2px(context, 30));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.love_pop_anim);
    }


    public void showPopupWindow(View parent) {
        parent.getLocationOnScreen(mLocation);
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + parent.getWidth(),
                mLocation[1] + parent.getHeight());
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.NO_GRAVITY,
                    mLocation[0] - this.getWidth(), mLocation[1] -
                            ((this.getHeight() - parent.getHeight()) / 2));
        }
        else {
            dismiss();
        }
    }


    @Override public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.digBtn:
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(0);
                }
                break;
            case R.id.commentBtn:
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(1);
                }
                break;
            default:
                break;
        }
    }
}
