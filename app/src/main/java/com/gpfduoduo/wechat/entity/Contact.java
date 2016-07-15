package com.gpfduoduo.wechat.entity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class Contact {
    public String nickName;

    public String sortKey;


    public Contact(String nickName) {
        this.nickName = nickName;
    }


    public Contact(String nickName, String sortKey) {
        this.nickName = nickName;
        this.sortKey = sortKey;
    }
}
