package com.gpfduoduo.wechat.ui.fragment.discovery;

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
public class DiscoveryFragment extends BaseDelayLoadFragment {
    public static DiscoveryFragment newInstance() {
        Bundle args = new Bundle();
        DiscoveryFragment fragment = new DiscoveryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container,
                false);
        return view;
    }


    @Override public void initLazyView(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            loadRootFragment(R.id.fragment_discovery_container,
                    DiscoveryDetailFragment.newInstance());
        }
    }
}
