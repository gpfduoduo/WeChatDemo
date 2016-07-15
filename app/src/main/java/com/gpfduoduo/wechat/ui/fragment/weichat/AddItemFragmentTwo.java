package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;

/**
 * Created by Administrator on 2016/7/12.
 */
public class AddItemFragmentTwo extends BaseFragment
        implements View.OnClickListener {

    public static AddItemFragmentTwo newInstance() {
        return new AddItemFragmentTwo();
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_add_item_two,
                container, false);
        return view;
    }


    @Override public void onClick(View v) {

    }
}
