package com.gpfduoduo.wechat.ui.fragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import com.gpfduoduo.wechat.R;

/**
 * Created by Administrator on 2016/6/28.
 */
public class BaseBackFragment extends BaseMainFragment {

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mBaseActivity.onBackPressed();
            }
        });
    }
}
