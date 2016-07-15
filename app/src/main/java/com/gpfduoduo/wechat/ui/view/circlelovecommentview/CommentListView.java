package com.gpfduoduo.wechat.ui.view.circlelovecommentview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by gpfduoduo on 2016/7/10.
 */
public class CommentListView extends ListView {
    public CommentListView(Context context) {
        super(context);
    }


    public CommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
