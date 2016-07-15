package com.gpfduoduo.wechat.ui.fragment.contact;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;
import com.guo.duoduo.randomtextview.RandomTextView;

/**
 * Created by Administrator on 2016/6/30.
 */
public class RadarFragment extends BaseBackFragment {

    public static RadarFragment newInstance() {
        RadarFragment fragment = new RadarFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_radar, container,
                false);

        initView(view);
        return view;
    }


    private void initView(View view) {
        final RandomTextView randomTextView
                = (RandomTextView) view.findViewById(R.id.random_textview);
        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener() {
                    @Override public void onRippleViewClicked(View view) {
                    }
                });

        new Handler().postDelayed(new Runnable() { //存在内存泄露的危险
            @Override public void run() {
                randomTextView.addKeyWord("彭丽媛");
                randomTextView.addKeyWord("习近平");
                randomTextView.show();
            }
        }, 500);
    }
}
