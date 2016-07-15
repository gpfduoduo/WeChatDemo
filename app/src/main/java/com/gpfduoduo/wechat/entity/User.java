package com.gpfduoduo.wechat.entity;

/**
 * Created by Administrator on 2016/7/9.
 */
public class User {
    public String id;
    public String name;
    public String headUrl;


    @Override public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", headUrl='" + headUrl + '\'' +
                '}';
    }
}
