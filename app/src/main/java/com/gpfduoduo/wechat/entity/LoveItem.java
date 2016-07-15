package com.gpfduoduo.wechat.entity;

/**
 * Created by Administrator on 2016/7/9.
 */
public class LoveItem {
    public String id;
    public User user;


    @Override public String toString() {
        return "LoveItem{" +
                "id='" + id + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
