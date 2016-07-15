package com.gpfduoduo.wechat.ui.fragment.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseDelayLoadFragment;
import com.gpfduoduo.wechat.ui.fragment.weichat.WeChatListFragment;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;

/**
 * Created by Administrator on 2016/6/27.
 */
public class ContactFragment extends BaseDelayLoadFragment {

    private static final String tag = ContactFragment.class.getSimpleName();


    public static ContactFragment newInstance() {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container,
                false);
        return view;
    }


    @Override public void initLazyView(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            loadRootFragment(R.id.fragment_contact_container,
                    ContactListFragment.newInstance());
        }
    }
}
