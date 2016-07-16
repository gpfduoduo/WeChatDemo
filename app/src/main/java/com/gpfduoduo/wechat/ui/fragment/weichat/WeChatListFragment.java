package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.Friend;
import com.gpfduoduo.wechat.ui.adapter.WeChatListAdapter;
import com.gpfduoduo.wechat.ui.fragment.BaseMainFragment;
import com.gpfduoduo.wechat.ui.fragment.contact.AddFriendFragment;
import com.gpfduoduo.wechat.ui.view.PlusActionProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/27.
 */
public class WeChatListFragment extends BaseMainFragment {

    private static final String tag = WeChatListFragment.class.getSimpleName();

    private ListView mListView;
    private WeChatListAdapter mAdapter;
    private Toolbar mToolbar;
    private List<Friend> mChatFriends = new ArrayList<>();

    private PlusActionProvider mActionProvider;


    public static WeChatListFragment newInstance() {
        Bundle args = new Bundle();
        WeChatListFragment fragment = new WeChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wechat_list, container,
                false);

        initView(view);
        initToolbar(view);
        return view;
    }


    private void initView(View view) {
        for (int i = 0; i < 20; i++) {
            mChatFriends.add(new Friend(getString(R.string.wife),
                    getString(R.string.hello)));
        }
        mListView = (ListView) view.findViewById(R.id.wechat_list);
        mAdapter = new WeChatListAdapter(getActivity(),
                R.layout.view_wechat_list_item, mChatFriends);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideTabBar();
                start(R.id.fragment_wechat_container,
                        WeChatDetailFragment.newInstance(
                                mChatFriends.get(position).nickName));
            }
        });
    }


    private void initToolbar(View view) {
        mToolbar = initToolBar(view, R.id.wechat_list_toolbar_include,
                R.menu.menu_main, getString(R.string.chat), null);

        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
        mActionProvider = new PlusActionProvider(getActivity());
        mActionProvider.setOnItemClickListener(
                new PlusActionProvider.ItemClickListener() {
                    @Override public void OnItemClickListener(String title) {
                        if (title.equals(
                                getString(R.string.action_qrcode))) { //启动二维码扫描
                            hideTabBar();
                            start(R.id.fragment_wechat_container,
                                    CameraFragment.newInstance(
                                            getString(R.string.chat)));
                        }
                        else if (title.equals(getString(R.string.action_add))) {
                            start(R.id.fragment_wechat_container,
                                    AddFriendFragment.newInstance(
                                            getString(R.string.chat),
                                            R.id.fragment_wechat_container));
                        }
                    }
                });
    }


    private Toolbar.
            OnMenuItemClickListener onMenuItemClick
            = new Toolbar.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add:
                    MenuItemCompat.setActionProvider(menuItem, mActionProvider);
                    break;
            }

            return true;
        }
    };
}
