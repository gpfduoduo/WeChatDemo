package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseMainFragment;

/**
 * Created by gpfduoduo on 2016/7/1.
 */
public class DiscoveryDetailFragment extends BaseMainFragment
        implements View.OnClickListener {

    public static DiscoveryDetailFragment newInstance() {
        DiscoveryDetailFragment fragment = new DiscoveryDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery_detail,
                container, false);

        initTabView(view);
        initItemView(view);

        return view;
    }


    private void initTabView(View view) {
        initToolBar(view, R.id.discovery_detail_viewstub, -1,
                getString(R.string.discover), null);
    }


    private void initItemView(View view) {
        setItem(view, R.id.discover_detail_item_circle,
                R.drawable.friend_circle, R.string.friend_circle, this);
        setItem(view, R.id.discover_detail_item_scan,
                R.drawable.icon_de_saoyisao, R.string.action_qrcode, this);
        setItem(view, R.id.discover_detail_item_game, R.drawable.icon_de_game,
                R.string.game, this);
        setItem(view, R.id.discover_detail_item_shop, R.drawable.icon_de_shop,
                R.string.shop, this);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover_detail_item_circle:
                hideTabBar();
                start(R.id.fragment_discovery_container,
                        DiscoveryCircleFragment.newInstance());
                break;
        }
    }
}
