package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import com.gpfduoduo.imageloader.ImageFolder;
import com.gpfduoduo.imageloader.ImageScanner;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.MainActivity;
import com.gpfduoduo.wechat.ui.adapter.LocalPhotoAdapter;
import com.gpfduoduo.wechat.ui.adapter.LocalPhotoFolderAdapter;
import com.gpfduoduo.wechat.ui.fragment.BaseVerticalAnimFragment;
import com.gpfduoduo.wechat.ui.fragment.event.FriendCircleSelectPhotoEvent;
import com.gpfduoduo.wechat.util.DeviceUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/7/4.
 */
public class LocalPhotoAlbumFragment extends BaseVerticalAnimFragment implements
        ImageScanner.ScanCompleteCallBack,
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    private static final String Fragment_FROM = "from";
    private static final int MAX_NUM = 9;
    private int mFrom = FriendCircleSelectPhotoEvent.PHOTO_TYPE.CIRCLE_SHARE;
    private ImageScanner mImageScanner;

    private GridView mGridView;
    private LocalPhotoAdapter mGridViewAdapter;
    private ListView mListView;
    private List<String> mList = new ArrayList<>();
    private List<ImageFolder> mListImageFolder;
    private LocalPhotoFolderAdapter mLocalPhotoFolderAdapter;
    private AnimatorSet inAnimatorSet = new AnimatorSet();
    private AnimatorSet outAnimatorSet = new AnimatorSet();
    private View mDimLayout;
    private boolean mIsFolderViewInit = false;
    private boolean mIsFolderViewShow = false;
    private View mFragmentView;
    private String mCurDir;
    private TextView mDirName;


    public static LocalPhotoAlbumFragment newInstance(int type) {
        LocalPhotoAlbumFragment fragment = new LocalPhotoAlbumFragment();
        Bundle args = new Bundle();
        args.putInt(Fragment_FROM, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFrom = args.getInt(Fragment_FROM);
        }
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_local_photo_album,
                container, false);

        initTabView(mFragmentView);
        initOtherView(mFragmentView);
        initData();
        return mFragmentView;
    }


    private void initTabView(View view) {
        Toolbar toolbar = initToolBar(view, R.id.local_photo_album_viewstub,
                R.menu.menu_select_from_photo, getString(R.string.all_photo),
                getString(R.string.photo));
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_select_from_photo_complete: //完成
                                FriendCircleSelectPhotoEvent event
                                        = new FriendCircleSelectPhotoEvent();
                                event.setType(mFrom);
                                event.setSelectedPhotos(
                                        mGridViewAdapter.getSelectedList());
                                EventBus.getDefault().post(event);
                                pop();
                                break;
                        }
                        return false;
                    }
                });
        initToolbarNav(toolbar);
    }


    private void initOtherView(View view) {
        mGridView = (GridView) view.findViewById(R.id.local_photo_gridview);
        mGridView.setOnItemClickListener(this);
        mDirName = ((TextView) view.findViewById(R.id.local_photo_folder_name));
        mDirName.setOnClickListener(this);
        ((TextView) view.findViewById(
                R.id.local_photo_preview)).setOnClickListener(this);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_photo_folder_name: // 选择相册
                if (!mIsFolderViewInit) { //保证只加载一次
                    initFolderListView(mFragmentView, mListImageFolder);
                    mIsFolderViewInit = true;
                    initAnimation(mDimLayout);
                }
                mLocalPhotoFolderAdapter.setSelected(mCurDir);
                toggle();
                break;
            case R.id.local_photo_preview: //进入大图浏览
                ArrayList<String> selected
                        = (ArrayList<String>) mGridViewAdapter.getSelectedList();
                start(R.id.fragment_discovery_container,
                        LocalPhotoBrowserFragment.newInstance(selected, 0));
                break;
        }
    }


    @Override
    public void scanComplete(File imageDir, List<ImageFolder> folders) {
        mCurDir = imageDir.getAbsolutePath();
        //最好不用直接mList = Arrays.asList()因为这样会导致mList.clear()等函数不可用
        mList.addAll(Arrays.asList(imageDir.list()));
        mGridViewAdapter = new LocalPhotoAdapter(this, mGridView,
                MyApplication.getContext(), R.layout.view_gridview_photo_item,
                mList, mCurDir);
        mGridView.setAdapter(mGridViewAdapter);
        mListImageFolder = folders;
        mDirName.setText(imageDir.getName());
    }


    private void initFolderListView(View view, List<ImageFolder> folders) {
        ViewStub selectFolderViewSub = (ViewStub) view.findViewById(
                R.id.local_photo_folder_stub);
        selectFolderViewSub.inflate();
        mListView = (ListView) view.findViewById(R.id.listview_folder);
        mDimLayout = view.findViewById(R.id.dim_layout);
        mLocalPhotoFolderAdapter = new LocalPhotoFolderAdapter(this,
                getActivity(), R.layout.view_listview_folder_item, folders);
        mListView.setAdapter(mLocalPhotoFolderAdapter);

        mDimLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (mIsFolderViewShow) {
                    toggle();
                    return true; //消费掉该事件
                }
                else {
                    return false;
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageFolder imageFolder = mListImageFolder.get(position);
                if (imageFolder == null) {
                    return;
                }
                mCurDir = imageFolder.getDir();
                Log.d(MainActivity.tag, "mCurDir = " + mCurDir);
                File dirFile = new File(mCurDir);
                mList.clear();
                mGridViewAdapter.notifyDataSetChanged();
                mGridViewAdapter.setDirPath(mCurDir);
                mList.addAll(Arrays.asList(dirFile.list()));
                mGridViewAdapter.notifyDataSetChanged();
                toggle();
                mDirName.setText(dirFile.getName());
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int selected = mGridViewAdapter.getSelectedList().size();
        if (selected >= MAX_NUM) {
            return;
        }
        mGridViewAdapter.selectPos(mList.get(position));
    }


    private void initData() {
        mImageScanner = new ImageScanner(MyApplication.getContext());
        mImageScanner.scanImages(this);
    }


    private void toggle() {
        if (mIsFolderViewShow) {
            outAnimatorSet.start();
            mIsFolderViewShow = false;
        }
        else {
            inAnimatorSet.start();
            mIsFolderViewShow = true;
        }
    }


    private void initAnimation(View dimLayout) {
        final ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator,
                transOutAnimator;
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (MyApplication.getContext()
                         .getTheme()
                         .resolveAttribute(android.R.attr.actionBarSize, tv,
                                 true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        int height = DeviceUtil.getScreenHeight(MyApplication.getContext()) -
                3 * actionBarHeight;
        alphaInAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0f, 0.7f);
        alphaOutAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0.7f, 0f);
        transInAnimator = ObjectAnimator.ofFloat(mListView, "translationY",
                height, 0);
        transOutAnimator = ObjectAnimator.ofFloat(mListView, "translationY", 0,
                height);

        LinearInterpolator linearInterpolator = new LinearInterpolator();

        inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
        inAnimatorSet.setDuration(300);
        inAnimatorSet.setInterpolator(linearInterpolator);
        outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
        outAnimatorSet.setDuration(300);
        outAnimatorSet.setInterpolator(linearInterpolator);

        addListener(transInAnimator, transOutAnimator);
    }


    private void addListener(ObjectAnimator transInAnimator, ObjectAnimator transOutAnimator) {
        transInAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                mListView.setVisibility(View.VISIBLE);
            }


            @Override public void onAnimationEnd(Animator animation) {

            }


            @Override public void onAnimationCancel(Animator animation) {

            }


            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        transOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }


            @Override public void onAnimationEnd(Animator animation) {
                mListView.setVisibility(View.GONE);
            }


            @Override public void onAnimationCancel(Animator animation) {

            }


            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
