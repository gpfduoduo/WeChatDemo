package com.gpfduoduo.wechat.ui.view.circleloveview;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;

/**
 * Created by gpfduoduo on 2016/7/9.
 */
public class NameClickable extends ClickableSpan
        implements View.OnClickListener {
    private final ISpanClick mListener;
    private int mPosition;


    public NameClickable(ISpanClick listener, int position) {
        mListener = listener;
        mPosition = position;
    }


    @Override public void onClick(View widget) {
        mListener.OnClick(mPosition);
    }


    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        int colorValue = MyApplication.getContext()
                                      .getResources()
                                      .getColor(R.color.gray_blue);
        ds.setColor(colorValue);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}