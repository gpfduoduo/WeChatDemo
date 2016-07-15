package com.gpfduoduo.wechat.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.util.DeviceUtil;

/**
 * 通用的弹出Dialog
 */
public class PopupDialog extends Dialog {

    protected Context mContext;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;


    public PopupDialog(Context context) {
        super(context, R.style.pop_dialogTheme);
        mWindow = this.getWindow();
        this.mContext = context;
        this.setCanceledOnTouchOutside(true);
        mLayoutParams = mWindow.getAttributes();
    }


    public View getContentView() {
        return this.getContentView();
    }


    /**
     * 从中间弹出
     */
    @Override public void show() {
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setAttributes(mLayoutParams);
        super.show();
    }


    /**
     * 从底部自下而上弹出
     */
    public void showAtBottom() {
        mWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mWindow.setWindowAnimations(R.style.pop_dialog_bottom_in_animation);
        mLayoutParams.width = DeviceUtil.getScreenWidth(getContext());
        mWindow.setAttributes(mLayoutParams);
        super.show();
    }


    /**
     * 从顶部自上而下弹出
     */
    public void showAtTop() {
        mWindow.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        mWindow.setWindowAnimations(R.style.pop_dialog_top_in_animation);
        mLayoutParams.width = DeviceUtil.getScreenWidth(getContext());
        mWindow.setAttributes(mLayoutParams);
        super.show();
    }
}
