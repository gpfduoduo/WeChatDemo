package com.gpfduoduo.wechat.ui.fragment.contact;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.Contact;
import com.gpfduoduo.wechat.ui.adapter.ContactListAdapter;
import com.gpfduoduo.wechat.ui.fragment.BaseMainFragment;
import com.gpfduoduo.wechat.util.MediaIndexer;
import com.gpfduoduo.wechat.util.PinyinUtil;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class ContactListFragment extends BaseMainFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mListView;
    private View mHeaderView;
    private ContactListAdapter mAdapter;
    private MediaIndexer mMediaIndex;
    private List<Contact> mContact;
    private FriendHandler mFriendHandler;

    private String[] contacts = new String[] { "老婆", "朋友", "同事", "同学", "亲戚",
            "家庭", "同桌", "老师", "老同学", "老子", "张三", "李四", "李五" };


    public static ContactListFragment newInstance() {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container,
                false);

        mFriendHandler = new FriendHandler(this);
        initTabView(view);
        initListView(view);
        setOnListener();
        getData();
        return view;
    }


    private void initListView(View view) {
        mListView = (ListView) view.findViewById(R.id.contact_list);
        mHeaderView = getActivity().getLayoutInflater()
                                   .inflate(R.layout.view_contact_header, null);
        mListView.addHeaderView(mHeaderView);
    }


    private void addData2List() {
        mMediaIndex = new MediaIndexer(mContact);
        mAdapter = new ContactListAdapter(getActivity(),
                R.layout.view_contact_list_item, mContact);
        mAdapter.setIndexer(mMediaIndex);
        mListView.setAdapter(mAdapter);
    }


    private void initTabView(View view) {
        Toolbar toolbar = initToolBar(view, R.id.contact_viewstub,
                R.menu.menu_contact_list, getString(R.string.contacts), null);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_contact_add:
                                hideTabBar();
                                start(R.id.fragment_contact_container,
                                        AddFriendFragment.newInstance());
                                break;
                        }
                        return true;
                    }
                });
    }


    private void setOnListener() {
        mListView.setOnItemClickListener(this);
        mHeaderView.findViewById(R.id.contact_layout_addfriend)
                   .setOnClickListener(this);
        mHeaderView.findViewById(R.id.contact_layout_search)
                   .setOnClickListener(this);
        mHeaderView.findViewById(R.id.contact_layout_group)
                   .setOnClickListener(this);
        mHeaderView.findViewById(R.id.contact_layout_public)
                   .setOnClickListener(this);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_layout_addfriend:
                break;
            case R.id.contact_layout_group:
                break;
            case R.id.contact_layout_public:
                break;
            case R.id.contact_layout_search:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = mContact.get(position - 1);
    }


    private void getData() {
        new Thread() {
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Message message = mFriendHandler.obtainMessage();
                message.obj = processContactData(contacts);
                mFriendHandler.sendMessage(message);
            }
        }.start();
    }


    private List<Contact> processContactData(String[] contacts) {
        List<Contact> list = new ArrayList<>();
        String name = null;
        String key = null;
        String sortKey = null;
        if (contacts != null) {
            for (int i = 0, len = contacts.length; i < len; i++) {
                name = contacts[i];
                try {
                    key = PinyinUtil.ChineseToSpell(name);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    key = null;
                }

                if (key != null) {
                    sortKey = PinyinUtil.getSortKey(key);
                    Contact contact = new Contact(name, sortKey);
                    list.add(contact);
                }
            }

            if (list != null && list.size() > 0) {
                Comparator<Contact> com = new Comparator<Contact>() {
                    RuleBasedCollator collator
                            = (RuleBasedCollator) Collator.getInstance(
                            Locale.US);


                    @Override public int compare(Contact lhs, Contact rhs) {
                        return collator.compare(lhs.sortKey, rhs.sortKey);
                    }
                };
                Collections.sort(list, com); // according to name
            }
        }
        return list;
    }


    @Override public void onDetach() {
        super.onDetach();
        mFriendHandler.removeCallbacksAndMessages(null);
    }


    static class FriendHandler extends Handler {
        WeakReference<ContactListFragment> mWeakReference;


        public FriendHandler(ContactListFragment fragment) {
            mWeakReference = new WeakReference<ContactListFragment>(fragment);
        }


        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ContactListFragment fragment = mWeakReference.get();
            if (msg == null || fragment == null ||
                    fragment.getActivity().isFinishing()) {
                return;
            }

            fragment.mContact = (List<Contact>) msg.obj;
            fragment.addData2List();
        }
    }
}
