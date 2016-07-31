package com.gpfduoduo.wechat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.Contact;
import com.gpfduoduo.wechat.ui.adapter.base.CustomAdapter;
import com.gpfduoduo.wechat.ui.adapter.base.ViewHolder;
import com.gpfduoduo.wechat.util.MediaIndexer;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class ContactListAdapter extends CustomAdapter<Contact> {

    /**
     * 字母表分组工具
     */
    private SectionIndexer mIndexer;


    public void setIndexer(MediaIndexer indexer) {
        mIndexer = indexer;
    }


    public ContactListAdapter(Context context, int resId, List<Contact> list) {
        super(context, resId, list);
    }


    @Override
    public void convert(ViewHolder viewHolder, Contact contact, int position) {
        TextView name = (TextView) viewHolder.getTextView(
                R.id.contack_list_name);

        TextView sortKey = viewHolder.getTextView(R.id.sort_key);
        LinearLayout layout = viewHolder.getLinearLayout(R.id.sort_key_layout);

        int section = mIndexer.getSectionForPosition(position);
        if (position == mIndexer.getPositionForSection(section)) {
            sortKey.setText(contact.sortKey);
            layout.setVisibility(View.VISIBLE);
        }
        else {
            layout.setVisibility(View.GONE);
        }

        name.setText(contact.nickName);
    }
}
