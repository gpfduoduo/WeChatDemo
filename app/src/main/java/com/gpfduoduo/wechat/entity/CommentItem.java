package com.gpfduoduo.wechat.entity;

import java.io.Serializable;

/**
 * Created by gpfduoduo on 2016/7/10.
 */
public class CommentItem implements Serializable {

    public String id;
    public User user;
    public User toReplyUser;
    public String content;
}
