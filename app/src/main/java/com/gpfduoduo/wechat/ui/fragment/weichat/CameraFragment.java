package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class CameraFragment extends BaseBackFragment {

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container,
                false);
        initToolbar(view);
        return view;
    }


    private void initToolbar(View view) {
        Toolbar toolbar = initToolBar(view, R.id.camera_title_viewstub, -1,
                getString(R.string.action_qrcode),
                getString(R.string.discover));
        initToolbarNav(toolbar);
    }


    @Override public void onPause() {
        super.onPause();
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
    }
}
