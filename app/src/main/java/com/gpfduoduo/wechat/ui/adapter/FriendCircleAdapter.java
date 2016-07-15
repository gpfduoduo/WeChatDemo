package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.CommentItem;
import com.gpfduoduo.wechat.entity.FriendCircle;
import com.gpfduoduo.wechat.entity.LoveItem;
import com.gpfduoduo.wechat.entity.User;
import com.gpfduoduo.wechat.ui.dialog.FriendCircleLovePopupWindow;
import com.gpfduoduo.wechat.ui.view.autogridview.AutoGridLayout;
import com.gpfduoduo.wechat.ui.view.circlelovecommentview.CommentListView;
import com.gpfduoduo.wechat.ui.view.circleloveview.ISpanClick;
import com.gpfduoduo.wechat.ui.view.circleloveview.LoveTextView;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/7.
 */
public class FriendCircleAdapter extends BaseAdapter {

    public static final int TYPE_PUBLIC_COMMENT = 0;
    public static final int TYPE_REPLY_COMMENT = 1;

    private Context mContext;
    private FriendCircleItemClickListener mOnItemClickListener;
    private List<FriendCircle> mFriendCircleList;
    private int mResId;
    private LayoutInflater mInflater;

    public interface FriendCircleItemClickListener {
        public void onFriendCircleItemClickListener(int friendCirclePos, int photoPos);
    }

    public interface OnCommentItemClick {
        public void onCommentItemClickListener(int type, int circlePos, int commentPos, User user);
    }

    private OnCommentItemClick mOnCommentItemClick;


    public void setOnCommentItemClick(OnCommentItemClick click) {
        this.mOnCommentItemClick = click;
    }


    public FriendCircleAdapter(Context context, int resLayoutId, List<FriendCircle> list, FriendCircleItemClickListener listener) {
        mContext = context;
        this.mFriendCircleList = list;
        this.mResId = resLayoutId;
        mOnItemClickListener = listener;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override public int getCount() {
        return mFriendCircleList.size();
    }


    @Override public Object getItem(int position) {
        return mFriendCircleList.get(position);
    }


    @Override public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(mResId, null);
            holder = new ViewHolder();
            holder.photos = (AutoGridLayout) convertView.findViewById(
                    R.id.friend_circle_item_photos);
            holder.loveImg = (ImageView) convertView.findViewById(
                    R.id.friend_circle_item_love);
            holder.lovePopupWindow = new FriendCircleLovePopupWindow(mContext);
            holder.mLoveCommentLayout = (LinearLayout) convertView.findViewById(
                    R.id.friend_circle_item_love_comment_layout);
            holder.mLoveTextView = (LoveTextView) convertView.findViewById(
                    R.id.friend_circle_item_loves);
            holder.mFriendCircleLoveAdapter = new FriendCircleLoveAdapter();
            holder.mLoveTextView.setAdapter(holder.mFriendCircleLoveAdapter);

            holder.mCommentListView
                    = (CommentListView) convertView.findViewById(
                    R.id.friend_circle_item_comment);
            holder.mCommentAdapter = new CommentAdapter(mContext);
            holder.mCommentListView.setAdapter(holder.mCommentAdapter);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder == null) return convertView;

        final FriendCircle friendCircle = this.mFriendCircleList.get(position);

        if (holder.lovePopupWindow == null) {
            return convertView;
        }

        final FriendCircleLovePopupWindow lovePopupWindow
                = holder.lovePopupWindow;
        //点赞和评论路对话框的显示
        lovePopupWindow.setOnItemClickListener(
                new PopupItemClickListener(position, friendCircle));

        holder.loveImg.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                lovePopupWindow.showPopupWindow(v);
            }
        });

        //朋友圈的图片显示
        FriendCirclePhotoAdapter photoAdapter = new FriendCirclePhotoAdapter(
                mContext, friendCircle.mPhotoList);
        holder.photos.setAdapter(photoAdapter);

        holder.photos.setOnItemClickListener(
                new AutoGridLayout.OnItemClickListener() {
                    @Override public void onItem(View view, int pos) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onFriendCircleItemClickListener(
                                    position, pos);
                        }
                    }
                });

        boolean isHaveLove = friendCircle.mLoveList.size() > 0 ? true : false;
        boolean isHaveComment = friendCircle.mCommentList.size() > 0
                                ? true
                                : false;
        if (isHaveComment || isHaveLove) {
            holder.mLoveCommentLayout.setVisibility(View.VISIBLE);
            if (isHaveLove) {
                holder.mLoveTextView.setVisibility(View.VISIBLE);
                //朋友圈的点赞效果, setSpanClick必须在前面
                holder.mLoveTextView.setSpanClick(new ISpanClick() {
                    @Override public void OnClick(int position) {
                        Toast.makeText(MyApplication.getContext(),
                                friendCircle.mLoveList.get(position).user.name,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                holder.mFriendCircleLoveAdapter.setLoveData(
                        friendCircle.mLoveList);
                holder.mFriendCircleLoveAdapter.notifyDataSetChanged();
            }
            else {
                holder.mLoveTextView.setVisibility(View.GONE);
            }
            //朋友圈的评论效果
            if (isHaveComment) {
                holder.mCommentListView.setVisibility(View.VISIBLE);
                holder.mCommentAdapter.setCommentData(
                        friendCircle.mCommentList);
                holder.mCommentAdapter.notifyDataSetChanged();
                holder.mCommentAdapter.setOnCommentClickListener(
                        new CommentAdapter.OnCommentClickListener() {
                            @Override
                            public void onCommentClickListener(int commentPos) {
                                CommentItem item
                                        = friendCircle.mCommentList.get(
                                        commentPos);
                                addComment(TYPE_REPLY_COMMENT, position,
                                        commentPos, item.user);
                            }
                        });
            }
            else {
                holder.mCommentListView.setVisibility(View.GONE);
            }
        }
        else {
            holder.mLoveCommentLayout.setVisibility(View.GONE);
        }

        return convertView;
    }


    static class ViewHolder {
        AutoGridLayout photos;
        ImageView loveImg;
        FriendCircleLovePopupWindow lovePopupWindow;
        LinearLayout mLoveCommentLayout;
        LoveTextView mLoveTextView;
        FriendCircleLoveAdapter mFriendCircleLoveAdapter;
        CommentListView mCommentListView;
        CommentAdapter mCommentAdapter;
    }


    /**
     * 添加评论
     *
     * @param type 评论的类型：直接进行评论还是回复 0是直接写， 1是回复
     * @param circlePos 朋友圈中该项的位置
     * @param commentPos 朋友圈中该评论项在评论终端位置
     * @param user 评论列表中的发表评论的用户（或者回复你的用户）
     */
    private void addComment(int type, int circlePos, int commentPos, User user) {
        if (mOnCommentItemClick != null) {
            mOnCommentItemClick.onCommentItemClickListener(type, circlePos,
                    commentPos, user);
        }
    }


    class PopupItemClickListener
            implements FriendCircleLovePopupWindow.OnItemClickListener {

        private int mCurCirclePos;


        public PopupItemClickListener(int itemPosition, FriendCircle friendCircle) {
            mCurCirclePos = itemPosition;
        }


        @Override public void onItemClick(int position) {
            switch (position) {
                case 0: //添加点赞
                    LoveItem loveItem = new LoveItem();
                    User user = new User();
                    user.name = "郭攀峰";
                    loveItem.user = user;
                    FriendCircle friendCircle = mFriendCircleList.get(
                            mCurCirclePos);
                    if (friendCircle == null) {
                        return;
                    }
                    friendCircle.mLoveList.add(loveItem);
                    notifyDataSetChanged();
                    break;
                case 1: //添加评论
                    addComment(TYPE_PUBLIC_COMMENT, mCurCirclePos, 0, null);
                    break;
            }
        }
    }
}
