package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.entity.CommentItem;
import com.gpfduoduo.wechat.entity.FriendCircle;
import com.gpfduoduo.wechat.entity.User;
import com.gpfduoduo.wechat.ui.adapter.FriendCircleAdapter;
import com.gpfduoduo.wechat.ui.dialog.AddFriendCircleDialog;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;
import com.gpfduoduo.wechat.ui.fragment.event.FriendCircleSelectPhotoEvent;
import com.gpfduoduo.wechat.ui.view.listview.BounceListView;
import com.gpfduoduo.wechat.util.CapturePhotoHelper;
import com.gpfduoduo.wechat.util.FolderManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by gpfduoduo on 2016/7/1.
 */
public class DiscoveryCircleFragment extends BaseBackFragment
        implements View.OnClickListener {

    private static final String tag
            = DiscoveryCircleFragment.class.getSimpleName();

    private static final int ANIM_DUR = 300;
    private final static int RUNTIME_PERMISSION_REQUEST_CODE = 0x1;
    private final static String EXTRA_RESTORE_PHOTO = "extra_restore_photo";

    private float mDownDistance;
    private File mRestorePhotoFile;

    private AddFriendCircleDialog mPhotoDialog;
    private ImageView mBackIcon;
    private ImageView mYouIcon;
    private ImageView mImgRefresh;
    private BounceListView mListView;
    private FriendCircleAdapter mAdapter;
    private List<FriendCircle> mFriendCircleList
            = new ArrayList<FriendCircle>();
    private View mHeaderView;

    private AnimatorSet mInAnimatorSet = new AnimatorSet();
    private AnimatorSet mOutAnimatorSet = new AnimatorSet();
    private AccelerateDecelerateInterpolator mInterpolator
            = new AccelerateDecelerateInterpolator();
    private LinearInterpolator mLinearInterpolator = new LinearInterpolator();

    private LinearLayout mInputLayout;
    private EditText mInputEdit;
    private TextView mInputSend;
    private int mCurPos;

    private int mListItemHeight;
    private int mSoftInputHeight;
    private int mScreenHeight;

    private CapturePhotoHelper mCapturePhotoHelper;


    public static DiscoveryCircleFragment newInstance() {
        DiscoveryCircleFragment fragment = new DiscoveryCircleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (mCapturePhotoHelper != null) {
                mRestorePhotoFile = (File) savedInstanceState.getSerializable(
                        EXTRA_RESTORE_PHOTO);
                mCapturePhotoHelper.setPhoto(mRestorePhotoFile);
            }
        }
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery_friend_circle,
                container, false);
        EventBus.getDefault().register(this);
        mDownDistance = MyApplication.getContext()
                                     .getResources()
                                     .getDimension(R.dimen.y70);
        initTabView(view);
        initOther(view);
        initListView(view);
        getSoftInputHeight(view);

        return view;
    }


    private void getSoftInputHeight(View view) {
        final RelativeLayout myLayout = (RelativeLayout) view.findViewById(
                R.id.friend_circle_root);
        myLayout.getViewTreeObserver().
                addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override public void onGlobalLayout() {
                                Rect r = new Rect();
                                myLayout.getWindowVisibleDisplayFrame(r);
                                mScreenHeight = myLayout.getRootView()
                                                        .getHeight();
                                int heightDifference = mScreenHeight -
                                        (r.bottom - r.top);
                                if (mSoftInputHeight == heightDifference) {
                                    return;
                                }
                                else {
                                    mSoftInputHeight = heightDifference;
                                    adjustListView();
                                }
                            }
                        });
    }


    @Override public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }


    private void initOther(View view) {
        mPhotoDialog = new AddFriendCircleDialog(getActivity(), this);
        mImgRefresh = (ImageView) view.findViewById(
                R.id.discovery_detail_circle_refresh);

        mInputLayout = (LinearLayout) view.findViewById(
                R.id.friend_circle_input_layout);
        mInputEdit = (EditText) view.findViewById(
                R.id.friend_circle_input_edit);
        mInputSend = (TextView) view.findViewById(
                R.id.friend_circle_input_send);
        mInputSend.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                FriendCircle friendCircle = mFriendCircleList.get(mCurPos);
                List<CommentItem> commentItems = friendCircle.mCommentList;
                User addUser = new User();
                addUser.name = "郭攀峰";
                addUser.id = "0";
                CommentItem item = new CommentItem();
                item.content = mInputEdit.getText().toString();
                item.id = "0";
                item.user = addUser;
                commentItems.add(item);
                mAdapter.notifyDataSetChanged();

                //隐藏输入框
                mInputEdit.setText("");
                hideInputLayout();
            }
        });
    }


    private void initListView(View view) {
        mListView = (BounceListView) view.findViewById(
                R.id.friend_circle_listView);
        mHeaderView = getActivity().getLayoutInflater()
                                   .inflate(R.layout.view_friend_circle_head,
                                           null);
        mListView.addHeaderView(mHeaderView);
        mBackIcon = (ImageView) mHeaderView.findViewById(
                R.id.friend_circle_back_icon);
        mYouIcon = (ImageView) mHeaderView.findViewById(
                R.id.friend_circle_you_head_icon);
        mYouIcon.setOnClickListener(this);
        mBackIcon.setOnClickListener(this);

        //into big image browse 进入大图浏览
        mAdapter = new FriendCircleAdapter(MyApplication.getContext(),
                R.layout.view_friend_circle_list_item, mFriendCircleList,
                new FriendCircleAdapter.FriendCircleItemClickListener() {
                    @Override
                    public void onFriendCircleItemClickListener(int friendCirclePos, int photoPos) {//预览照片
                        hideInputLayout();
                        List<String> photos = mFriendCircleList.get(
                                friendCirclePos).mPhotoList;
                        start(R.id.fragment_discovery_container,
                                LocalPhotoBrowserFragment.newInstance(
                                        new ArrayList<String>(photos),
                                        photoPos));
                    }
                });
        mListView.setAdapter(mAdapter);

        //朋友圈评论列表添加评论，或者是对其他人进行回复
        mAdapter.setOnCommentItemClick(
                new FriendCircleAdapter.OnCommentItemClick() {
                    @Override
                    public void onCommentItemClickListener(int type, int circlePos, int commentPos, User user) {
                        //显示输入框
                        mInputLayout.setVisibility(View.VISIBLE);
                        mCurPos = circlePos;
                        measureLocationParam(type, circlePos);
                        showInput(mInputEdit);
                    }
                });

        //下拉刷新 左上角显示更新旋转的图标
        mListView.setDownListener(new BounceListView.OnDownListener() {
            @Override public void onDownListener(int distance) {

            }


            @Override public void onDownStart() {
                showImgRefresh(true);
            }


            @Override public void onDownEnd() {
                showImgRefresh(false);
            }
        });

        //点击聊天界面 输入框消息
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                hideInputLayout();
                return false;
            }
        });
    }


    private void adjustListView() {
        final int listOffset = mScreenHeight - mListItemHeight -
                mSoftInputHeight -
                mInputLayout.getHeight();
        if (mListView != null) {
            final int headCount = mListView.getHeaderViewsCount();
            Log.d(tag, "adjust list view soft input height  = " +
                    mSoftInputHeight +
                    "; input " +
                    "layout height = " + mInputLayout.getHeight() +
                    "; " +
                    "currentSelection =  " + (mCurPos + headCount) +
                    "; list offset = " + listOffset + "; screenHeight" +
                    " = " + mScreenHeight);
            mListView.post(new Runnable() {
                @Override public void run() {
                    mListView.setSelectionFromTop((mCurPos + headCount),
                            listOffset);
                    Log.d(tag, "first visible = " +
                            mListView.getFirstVisiblePosition());
                }
            });
        }
    }


    private void measureLocationParam(int type, int circlePos) {
        int firstPos = mListView.getFirstVisiblePosition();
        if (firstPos < 0) {
            firstPos = 0;
        }

        Log.d(tag, "circlePos = " + circlePos + "; first " +
                "pos = " + firstPos + "; list view size = " +
                mListView.getChildCount() + "; head view height = " +
                mHeaderView.getMeasuredHeight() + "; adapter count= " +
                mListView.getAdapter().getCount());

        int headCount = mListView.getHeaderViewsCount();
        Log.d(tag, "head count = " + headCount);

        View listItem = mListView.getChildAt(circlePos + headCount - firstPos);

        if (type == FriendCircleAdapter.TYPE_PUBLIC_COMMENT) {
            if (listItem != null) {
                mListItemHeight = listItem.getMeasuredHeight();
                Log.d(tag, "list item height = " + mListItemHeight);
            }
        }
        else if (type == FriendCircleAdapter.TYPE_REPLY_COMMENT) {

        }
    }


    @Override public boolean onBackPressedSupport() {
        hideInputLayout();
        return super.onBackPressedSupport();
    }


    private void hideInputLayout() {
        hideInput(mInputEdit);
        mInputLayout.setVisibility(View.GONE);
    }


    private void initTabView(View view) {
        Toolbar toolbar = initToolBar(view,
                R.id.discovery_friend_circle_viewstub,
                R.menu.menu_discovery_friend_circle,
                getString(R.string.friend_circle),
                getString(R.string.discover));

        initToolbarNav(toolbar);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_discovery_friend_circle:
                                if (mPhotoDialog != null) {
                                    mPhotoDialog.showAtBottom();
                                }
                                break;
                        }
                        return false;
                    }
                });
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCapturePhotoHelper != null) {
            mRestorePhotoFile = mCapturePhotoHelper.getPhoto();
            if (mRestorePhotoFile != null) {
                outState.putSerializable(EXTRA_RESTORE_PHOTO,
                        mRestorePhotoFile);
            }
        }
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_circle_item_cancel: //消息
                break;
            case R.id.friend_circle_item_little_video://小视频
                break;
            case R.id.friend_circle_item_select_from_photo: //选择本地图片
                start(R.id.fragment_discovery_container,
                        LocalPhotoAlbumFragment.newInstance());
                break;
            case R.id.friend_circle_item_take_photo: //进入拍照页面
                takePicture();
                break;
            case R.id.friend_circle_you_head_icon: //右上角的小图标
                break;
            case R.id.friend_circle_back_icon: //最上面的背景
                break;
        }
        if (mPhotoDialog != null) {
            mPhotoDialog.cancel();
        }
        hideInputLayout();
    }


    private void turnOnCamera() {
        if (mCapturePhotoHelper == null) {
            mCapturePhotoHelper = new CapturePhotoHelper(
                    DiscoveryCircleFragment.this,
                    FolderManager.getPhotoFolder());
        }
        mCapturePhotoHelper.capture();
    }


    private void takePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {//检查是否有写入SD卡的授权
                turnOnCamera();
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        mBaseActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                }
                requestPermission();
            }
        }
        else {
            turnOnCamera();
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(mBaseActivity,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                RUNTIME_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CapturePhotoHelper.CAPTURE_PHOTO_REQUEST_CODE) {
            if (mCapturePhotoHelper == null) {
                return;
            }
            File photo = mCapturePhotoHelper.getPhoto();
            if (photo == null) {
                return;
            }
            String photoPath = photo.getAbsolutePath();
            if (TextUtils.isEmpty(photoPath)) {
                return;
            }

            if (photo.length() <= 0) {
                photo.delete();
                return;
            }

            mBaseActivity.sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://" + photoPath)));

            FriendCircle friendCircle = new FriendCircle();
            List<String> photos = new ArrayList<>();
            photos.add(photoPath);
            friendCircle.mPhotoList = photos;
            mFriendCircleList.add(0, friendCircle);
            mAdapter.notifyDataSetChanged();
        }
        mPhotoDialog.dismiss();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
            for (int index = 0; index < permissions.length; index++) {
                String permission = permissions[index];
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(
                        permission)) {
                    if (grantResults[index] ==
                            PackageManager.PERMISSION_GRANTED) {
                        turnOnCamera();
                    }
                    else {
                        showMissingPermissionDialog();
                    }
                }
            }
        }
    }


    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.help_content);

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.setPositiveButton(R.string.me_setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        turnOnSettings();
                    }
                });

        builder.show();
    }


    private void turnOnSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mBaseActivity.getPackageName()));
        startActivity(intent);
    }


    @Subscribe
    public void onEventMainThread(FriendCircleSelectPhotoEvent event) {
        FriendCircle friendCircle = new FriendCircle();
        friendCircle.mPhotoList = event.getSelectedPhotos();
        mFriendCircleList.add(0, friendCircle);
        mAdapter.notifyDataSetChanged();
    }


    private void showImgRefresh(boolean show) {
        final ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator,
                transOutAnimator, rotateAnim;
        float start = mDownDistance;
        float end = 0;
        if (show) {
            mOutAnimatorSet.end();
            start = 0;
            end = mDownDistance;
            alphaInAnimator = ObjectAnimator.ofFloat(mImgRefresh, "alpha", 0f,
                    1.0f);
            transInAnimator = ObjectAnimator.ofFloat(mImgRefresh,
                    "translationY", start, end);
            rotateAnim = ObjectAnimator.ofFloat(mImgRefresh, "rotation", 0,
                    360);
            rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnim.setInterpolator(mLinearInterpolator);
            mInAnimatorSet.play(transInAnimator)
                          .with(alphaInAnimator)
                          .with(rotateAnim);
            mInAnimatorSet.setDuration(ANIM_DUR);
            mInAnimatorSet.setInterpolator(mInterpolator);
            mInAnimatorSet.start();
        }
        else {
            mInAnimatorSet.end();
            alphaOutAnimator = ObjectAnimator.ofFloat(mImgRefresh, "alpha",
                    1.0f, 0f);
            transOutAnimator = ObjectAnimator.ofFloat(mImgRefresh,
                    "translationY", start, end);
            mOutAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
            mOutAnimatorSet.setDuration(ANIM_DUR);
            mOutAnimatorSet.setInterpolator(mInterpolator);
            mOutAnimatorSet.start();
        }
    }
}
