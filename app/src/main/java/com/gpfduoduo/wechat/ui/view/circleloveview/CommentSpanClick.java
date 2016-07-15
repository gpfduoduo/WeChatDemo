package com.gpfduoduo.wechat.ui.view.circleloveview;

import android.widget.Toast;
import com.gpfduoduo.wechat.MyApplication;

/**
 * Created by gpfduoduo on 2016/7/14.
 */
public class CommentSpanClick implements ISpanClick {

    String name;
    String id;


    public CommentSpanClick(String name, String id) {
        this.name = name;
        this.id = id;
    }


    @Override public void OnClick(int position) {
        Toast.makeText(MyApplication.getContext(), name, Toast.LENGTH_SHORT)
             .show();
        ;
    }
}
