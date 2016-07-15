package com.gpfduoduo.wechat.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/7.
 */
public class FriendCircle {

    public User mUser;
    public String mId;
    public String mName;
    public String mContent;
    public String mPortrait;
    public String mTime;
    public List<String> mPhotoList;
    public List<LoveItem> mLoveList = new ArrayList<>();
    public List<CommentItem> mCommentList = new ArrayList<>();
}
