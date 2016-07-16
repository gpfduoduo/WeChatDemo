package com.gpfduoduo.wechat.ui.view;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import com.gpfduoduo.wechat.R;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class PlusActionProvider extends ActionProvider {

    private static final String tag = PlusActionProvider.class.getSimpleName();

    private Context context;

    public interface ItemClickListener {
        public void OnItemClickListener(String title);
    }

    private ItemClickListener mItemClickListener;


    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }


    public PlusActionProvider(Context context) {
        super(context);
        this.context = context;
    }


    @Override public View onCreateActionView() {
        return null;
    }


    @Override public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        subMenu.add(context.getString(R.string.action_add))
               .setIcon(R.drawable.action_add_friend)
               .setOnMenuItemClickListener(
                       new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               if (mItemClickListener != null) {
                                   mItemClickListener.OnItemClickListener(
                                           item.getTitle().toString());
                               }
                               return false;
                           }
                       });
        subMenu.add(context.getString(R.string.action_card))
               .setIcon(R.drawable.action_card)
               .setOnMenuItemClickListener(
                       new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               return false;
                           }
                       });
        subMenu.add(context.getString(R.string.action_qrcode))
               .setIcon(R.drawable.action_qrcode)
               .setOnMenuItemClickListener(
                       new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               if (mItemClickListener != null) {
                                   mItemClickListener.OnItemClickListener(
                                           item.getTitle().toString());
                               }
                               return false;
                           }
                       });
        subMenu.add(context.getString(R.string.action_setting))
               .setIcon(R.drawable.action_settings)
               .setOnMenuItemClickListener(
                       new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               return false;
                           }
                       });
    }


    @Override public boolean hasSubMenu() {
        return true;
    }
}
