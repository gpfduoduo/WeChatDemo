package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.view.viewpager.ViewPagerFixed;
import com.gpfduoduo.fragmentutil.ui.BaseFragment;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by gpfduoduo on 2016/7/6.
 */
public class LocalPhotoBrowserFragment extends BaseFragment
        implements ViewPager.OnPageChangeListener {

    private static final String tag
            = LocalPhotoBrowserFragment.class.getSimpleName();

    private static final String PREVIEW_PHOTOS = "preview_photos";
    private static final String PREVIEW_POS = "preview_pos";

    private ArrayList<String> mPreviewList;
    private int mCurPos = 0;

    private ViewPagerFixed mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;


    public static LocalPhotoBrowserFragment newInstance(ArrayList<String> photos, int position) {
        LocalPhotoBrowserFragment fragment = new LocalPhotoBrowserFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(PREVIEW_PHOTOS, photos);
        args.putInt(PREVIEW_POS, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPreviewList = bundle.getStringArrayList(PREVIEW_PHOTOS);
            mCurPos = bundle.getInt(PREVIEW_POS);
        }
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_borwser, container,
                false);

        initView(view);
        return view;
    }


    @Override public void onDetach() {
        super.onDetach();
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        Glide.with(this).onLowMemory();
    }


    @Override public void onLowMemory() {
        super.onLowMemory();
        Log.e(tag, "onLowMemory");
    }


    private void initView(View view) {
        mViewPager = (ViewPagerFixed) view.findViewById(
                R.id.friend_circle_browser_viewpager);
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(mCurPos);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override public void onPageSelected(int position) {

    }


    @Override public void onPageScrollStateChanged(int state) {

    }


    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mBaseActivity)
                                      .inflate(R.layout.view_image_browser,
                                              container, false);
            PhotoView photoView = (PhotoView) view.findViewById(
                    R.id.big_img_browser);

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    pop();
                }
            });

            Glide.with(LocalPhotoBrowserFragment.this)
                 .load(mPreviewList.get(position))
                 .placeholder(R.drawable.ic_photo_loading)
                 .centerCrop()
                 .into(photoView);

            container.addView(view);
            return view;
        }


        @Override public int getCount() {
            return mPreviewList.size();
        }


        @Override public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override public int getItemPosition(Object object) {
            if (mPreviewList.size() >= 0) return POSITION_NONE; //必须添加这个

            return super.getItemPosition(object);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
