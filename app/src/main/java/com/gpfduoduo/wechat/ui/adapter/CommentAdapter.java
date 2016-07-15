package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.CommentItem;
import com.gpfduoduo.wechat.entity.User;
import com.gpfduoduo.wechat.ui.view.circleloveview.CircleMovementMethod;
import com.gpfduoduo.wechat.ui.view.circleloveview.CommentSpanClick;
import com.gpfduoduo.wechat.ui.view.circleloveview.NameClickable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/10.
 */
public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommentItem> mCommentItemList = new ArrayList<>();

    private OnCommentClickListener mCommentClickListener;

    public interface OnCommentClickListener {
        public void onCommentClickListener(int position);
    }


    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.mCommentClickListener = listener;
    }


    public CommentAdapter(Context context) {
        mContext = context;
    }


    public void setCommentData(List<CommentItem> list) {
        mCommentItemList = list;
    }


    @Override public int getCount() {
        return mCommentItemList.size();
    }


    @Override public Object getItem(int position) {
        return mCommentItemList.get(position);
    }


    @Override public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext,
                    R.layout.view_friend_circle_item_comment, null);
            viewHolder.commentTv = (TextView) convertView.findViewById(
                    R.id.commentTv);
            viewHolder.circleMovementMethod = new CircleMovementMethod(
                    R.color.name_selector_color, R.color.name_selector_color);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommentItem item = mCommentItemList.get(position);
        String name = item.user.name;
        String id = item.id;
        String toReplayName = null;
        String toReplayId = null;
        User user = item.toReplyUser;
        if (user != null) {
            toReplayName = user.name;
            toReplayId = user.id;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, id, 0));

        if (!TextUtils.isEmpty(toReplayName)) {
            builder.append(" 回复 ");
            builder.append(setClickableSpan(toReplayName, toReplayId, 1));
        }

        builder.append(": ");
        String contentBodyStr = item.content;
        builder.append(contentBodyStr);
        viewHolder.commentTv.setText(builder);

        final CircleMovementMethod circleMovementMethod
                = viewHolder.circleMovementMethod;
        viewHolder.commentTv.setMovementMethod(circleMovementMethod);

        viewHolder.commentTv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //很重要，否则点击name也会弹出输入框
                if (mCommentClickListener != null &&
                        circleMovementMethod.isPassToTv()) {
                    mCommentClickListener.onCommentClickListener(position);
                }
            }
        });

        return convertView;
    }


    private SpannableString setClickableSpan(String textStr, String id, int position) {
        SpannableString spannableString = new SpannableString(textStr);
        spannableString.setSpan(
                new NameClickable(new CommentSpanClick(textStr, id), position),
                0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }


    class ViewHolder {
        TextView commentTv;
        CircleMovementMethod circleMovementMethod;
    }
}
