package com.gpfduoduo.wechat.ui.fragment.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;

/**
 * Created by gpfduoduo on 2016/6/30.
 */
public class AddFriendFragment extends BaseBackFragment
        implements View.OnClickListener {

    private static final String LEFT_TITLE = "left_title";
    private static final String CONTAINER_ID = "container_id";
    private String mLeftTitle;
    private int mContainerId;


    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    public static AddFriendFragment newInstance(String leftTitle, int containerId) {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putString(LEFT_TITLE, leftTitle);
        args.putInt(CONTAINER_ID, containerId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mLeftTitle = bundle.getString(LEFT_TITLE);
            mContainerId = bundle.getInt(CONTAINER_ID);
        }
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container,
                false);

        initToolbar(view);
        initOther(view);
        return view;
    }


    private void initToolbar(View view) {
        Toolbar toolbar = initToolBar(view,
                R.id.contact_add_friend_toolbar_viewstub, -1,
                getString(R.string.add_friend), TextUtils.isEmpty(mLeftTitle)
                                                ? getString(R.string.contacts)
                                                : mLeftTitle);
        initToolbarNav(toolbar);
    }


    private void initOther(View view) {
        RelativeLayout radarInclude = (RelativeLayout) view.findViewById(
                R.id.contact_add_friend_radar);
        setItem(radarInclude, R.drawable.icon_de_nearby, R.string.radar,
                R.string.add_nearby);

        RelativeLayout publicInclude = (RelativeLayout) view.findViewById(
                R.id.contact_add_friend_public);
        setItem(publicInclude, R.drawable.icon_public, R.string.public_id,
                R.string.public_detail);

        RelativeLayout chatInclude = (RelativeLayout) view.findViewById(
                R.id.contact_add_friend_qunliao);
        setItem(chatInclude, R.drawable.icon_qunliao, R.string.face_to_face,
                R.string.face_to_face_detail);

        RelativeLayout scanInclude = (RelativeLayout) view.findViewById(
                R.id.contact_add_friend_scan);
        setItem(scanInclude, R.drawable.icon_de_saoyisao,
                R.string.action_qrcode, R.string.action_qrcode_detail);

        setClickListener(publicInclude, radarInclude, chatInclude, scanInclude);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_add_friend_radar:
                start(mContainerId, RadarFragment.newInstance());
                break;
        }
    }


    private void setClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }


    private void setItem(View include, int iconId, int titleId, int subTitleId) {
        ((ImageView) include.findViewById(
                R.id.wechat_list_img)).setBackgroundDrawable(
                getActivity().getResources().getDrawable(iconId));
        ((TextView) include.findViewById(R.id.wechat_list_friend_name)).setText(
                titleId);
        ((TextView) include.findViewById(R.id.wechat_list_content)).setText(
                subTitleId);
    }
}
