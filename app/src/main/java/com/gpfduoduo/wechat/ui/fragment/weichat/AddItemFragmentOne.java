package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.discovery.LocalPhotoAlbumFragment;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;

/**
 * Created by gpfduoduo on 2016/7/12.
 */
public class AddItemFragmentOne extends BaseFragment
        implements View.OnClickListener {

    private static final String tag = AddItemFragmentOne.class.getSimpleName();


    public static AddItemFragmentOne newInstance() {
        AddItemFragmentOne fragment = new AddItemFragmentOne();

        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_add_item, container,
                false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_fav)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_gift)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_photo)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_chat_video)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_chat_audio)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_location)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_audio_input)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(
                R.id.wechat_add_item_personalcard)).setOnClickListener(this);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_add_item_fav:
                break;
            case R.id.wechat_add_item_gift:
                break;
            case R.id.wechat_add_item_photo: //聊天页面进入相册
                ((WeChatDetailFragment) getParentFragment()).start(
                        R.id.fragment_wechat_container,
                        LocalPhotoAlbumFragment.newInstance());
                break;
            case R.id.wechat_add_item_chat_video:
                break;
            case R.id.wechat_add_item_chat_audio:
                break;
            case R.id.wechat_add_item_location:
                break;
            case R.id.wechat_add_item_audio_input:
                break;
            case R.id.wechat_add_item_personalcard:
                break;
        }
    }
}
