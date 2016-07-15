package com.gpfduoduo.wechat.ui.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.LoveItem;
import com.gpfduoduo.wechat.ui.view.circleloveview.CircleMovementMethod;
import com.gpfduoduo.wechat.ui.view.circleloveview.LoveTextView;
import com.gpfduoduo.wechat.ui.view.circleloveview.NameClickable;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/9.
 */
public class FriendCircleLoveAdapter {

    private LoveTextView mLoveTextView;
    private List<LoveItem> mLoveItemList;


    public void bindLoveTextView(LoveTextView view) {
        mLoveTextView = view;
    }


    public void setLoveData(List<LoveItem> loveData) {
        mLoveItemList = loveData;
    }


    public int getSize() {
        if (mLoveItemList == null) return 0;
        return mLoveItemList.size();
    }


    public void notifyDataSetChanged() {
        if (mLoveItemList == null || mLoveItemList.size() < 1) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getLoveImage());//点赞的图标
        LoveItem item = null;
        for (int i = 0; i < getSize(); i++) { //添加点赞人的信息
            item = mLoveItemList.get(i);
            if (item != null) {
                builder.append(setClickableSpan(item.user.name, i));
                if (i != mLoveItemList.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        mLoveTextView.setText(builder);
        mLoveTextView.setMovementMethod(
                new CircleMovementMethod(R.color.name_selector_color));
    }


    private SpannableString setClickableSpan(String textStr, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(
                new NameClickable(mLoveTextView.getSpanClickListener(),
                        position), 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }


    private SpannableString getLoveImage() {
        SpannableString loveSpannable = new SpannableString("  ");
        loveSpannable.setSpan(
                new ImageSpan(MyApplication.getContext(), R.drawable.love_tips,
                        DynamicDrawableSpan.ALIGN_BASELINE), 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return loveSpannable;
    }
}
