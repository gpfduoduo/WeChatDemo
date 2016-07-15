package com.gpfduoduo.wechat.ui.view.circleloveview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.gpfduoduo.wechat.ui.adapter.FriendCircleLoveAdapter;

/**
 * Created by gpfduoduo on 2016/7/9.
 */
public class LoveTextView extends TextView {

    private ISpanClick mSpanClickListener;


    public LoveTextView(Context context) {
        super(context);
    }


    public LoveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public LoveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public ISpanClick getSpanClickListener() {
        return mSpanClickListener;
    }


    public void setSpanClick(ISpanClick click) {
        mSpanClickListener = click;
    }


    public void setAdapter(FriendCircleLoveAdapter adapter) {
        adapter.bindLoveTextView(this);
    }
}
