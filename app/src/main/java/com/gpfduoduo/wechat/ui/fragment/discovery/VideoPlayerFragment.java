package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;

/**
 * Created by gpfduoduo on 2016/7/22.
 */
public class VideoPlayerFragment extends BaseBackFragment {

    public static VideoPlayerFragment newInstance() {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override public void onDetach() {
        super.onDetach();
    }
}
