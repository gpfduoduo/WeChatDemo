package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseVerticalAnimFragment;

/**
 * Created by gpfduoduo on 2016/7/17.
 */
public class LittleVideoFragment extends BaseVerticalAnimFragment {

    private SurfaceView mSurfaceView;


    public static LittleVideoFragment newInstance() {
        LittleVideoFragment fragment = new LittleVideoFragment();
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_little_video, container,
                false);

        initView(view);
        return view;
    }


    private void initView(View view) {
        mSurfaceView = (SurfaceView) view.findViewById(
                R.id.little_video_record_preview);
        
    }
}
