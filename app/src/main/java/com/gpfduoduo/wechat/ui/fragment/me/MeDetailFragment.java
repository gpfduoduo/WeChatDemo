package com.gpfduoduo.wechat.ui.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseMainFragment;

/**
 * Created by gpfduoduo on 2016/7/11.
 */
public class MeDetailFragment extends BaseMainFragment
        implements View.OnClickListener {

    public static MeDetailFragment newInstance() {
        MeDetailFragment fragment = new MeDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_detail, container,
                false);

        initTabView(view);
        initItemView(view);

        return view;
    }


    private void initTabView(View view) {
        initToolBar(view, R.id.me_detail_viewstub, -1, getString(R.string.me),
                null);
    }


    private void initItemView(View view) {
        setItem(view, R.id.me_detail_item_album, R.drawable.icon_me_photo,
                R.string.me_album, this);
        setItem(view, R.id.me_detail_item_fav, R.drawable.icon_me_collect,
                R.string.me_collect, this);
        setItem(view, R.id.me_detail_item_wallet, R.drawable.icon_me_money,
                R.string.me_money, this);
        setItem(view, R.id.me_detail_item_card, R.drawable.icon_me_card,
                R.string.me_card, this);
        setItem(view, R.id.me_detail_item_faces, R.drawable.icon_me_smail,
                R.string.me_smile, this);
        setItem(view, R.id.me_detail_item_setting, R.drawable.icon_me_setting,
                R.string.me_setting, this);
    }


    @Override public void onClick(View v) {

    }
}
