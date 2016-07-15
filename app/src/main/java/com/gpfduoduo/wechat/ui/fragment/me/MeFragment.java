package com.gpfduoduo.wechat.ui.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseDelayLoadFragment;

/**
 * Created by Administrator on 2016/6/27.
 */
public class MeFragment extends BaseDelayLoadFragment {

    public static MeFragment newInstance() {
        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }


    @Override public void initLazyView(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            loadRootFragment(R.id.fragment_me_container,
                    MeDetailFragment.newInstance());
        }
    }
}
