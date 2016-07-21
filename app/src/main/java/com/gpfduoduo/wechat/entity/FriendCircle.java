package com.gpfduoduo.wechat.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/7.
 */
public class FriendCircle {

    public interface CONTENT_TYPE {
        public static final int IMAGE = 0;
        public static final int VIDEO = 1;
        public static final int URL = 3;
        public static final int MUSIC = 4;
    }

    public User mUser;
    public String mId;
    public String mName;
    public String mContent;
    public String mPortrait;
    public String mTime;
    public String videoPath;
    public int contentType = CONTENT_TYPE.IMAGE;
    public List<String> mPhotoList = new ArrayList<>();
    public List<LoveItem> mLoveList = new ArrayList<>();
    public List<CommentItem> mCommentList = new ArrayList<>();
}
