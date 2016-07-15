package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseDelayLoadFragment;

/**
 * Created by gpfduoduo on 2016/6/27.
 */
public class WeChatFragment extends BaseDelayLoadFragment {

    private static final String tag = WeChatFragment.class.getSimpleName();


    public static WeChatFragment newInstance() {
        Bundle args = new Bundle();
        WeChatFragment fragment = new WeChatFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wechat, container,
                false);
        return view;
    }


    @Override public void initLazyView(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            loadRootFragment(R.id.fragment_wechat_container,
                    WeChatListFragment.newInstance());
        }
    }
}
