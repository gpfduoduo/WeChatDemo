package com.gpfduoduo.wechat.ui.fragment.discovery;

import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseVerticalAnimFragment;
import com.gpfduoduo.wechat.ui.view.textdrawable.TextDrawable;
import com.gpfduoduo.wechat.util.DeviceUtil;
import com.gpfduoduo.wechat.util.camera.MediaRecorderBase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gpfduoduo on 2016/7/17.
 */
public class LittleVideoFragment extends BaseVerticalAnimFragment
        implements MediaRecorderBase.OnErrorListener {

    private static final String tag = LittleVideoFragment.class.getSimpleName();

    private static final int HANDLE_HIDE_RECORD_FOCUS = 2;
    private static final int HANDLE_PROGRESS = 10;
    private static final int MAX_TIME = 100;

    private SurfaceView mSurfaceView;
    private ImageView mFocusImage;
    private ImageView mRecordController;
    private RelativeLayout mBottomLayout;
    private ProgressBar mProgress;

    private boolean mIsCreated = false;
    private volatile boolean mIsReleased;
    private int mFocusWidth;
    private int mScreenWidth;
    private int mPro;
    private int mProSecond;

    private MediaRecorderBase mMediaRecorder;
    private Animation mFocusAnimation;
    private Animation mScaleAnimation;

    private LittleVideoHandler mHandler;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;


    public static LittleVideoFragment newInstance() {
        LittleVideoFragment fragment = new LittleVideoFragment();
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity.getWindow()
                     .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFocusWidth = (int) mBaseActivity.getResources()
                                         .getDimension(
                                                 R.dimen.little_video_focus_width);
        ;
        mScreenWidth = DeviceUtil.getScreenWidth(getActivity());
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_little_video, container,
                false);

        mHandler = new LittleVideoHandler(this);
        initToolbar(view);
        initView(view);
        mIsCreated = true;
        return view;
    }


    @Override public void onResume() {
        super.onResume();
        Log.i(tag, "onResume");
        if (mMediaRecorder == null) {
            initMediaRecorder();
        }
        else {
            mMediaRecorder.prepare();
        }
    }


    @Override public void onPause() {
        super.onPause();
        Log.i(tag, "onPause");
        if (!mIsReleased) {
            if (mMediaRecorder != null) mMediaRecorder.release();
        }
        mIsReleased = false;
    }


    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderBase();
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    private void initToolbar(View view) {
        Toolbar toolbar = initToolBar(view, R.id.little_video_viewstub, -1,
                null, getString(R.string.cancel));
        initToolbarNav(toolbar);
    }


    private void initView(View view) {
        mSurfaceView = (SurfaceView) view.findViewById(
                R.id.little_video_record_preview);
        mFocusImage = (ImageView) view.findViewById(
                R.id.little_video_record_focusing);
        mFocusImage.setImageResource(R.drawable.video_focus);
        mRecordController = (ImageView) view.findViewById(
                R.id.little_video_record_controller);
        mBottomLayout = (RelativeLayout) view.findViewById(
                R.id.little_video_bottom_layout);

        mBottomLayout.setOnTouchListener(mOnVideoControllerTouchListener);
        mSurfaceView.setOnTouchListener(mOnSurfaceViewTouchListener);

        mRecordController.setImageDrawable(getPressToRecordDrawable());

        initProgress(view);
        measureSurfaceView();
    }


    private void initProgress(View view) {
        mProgress = (ProgressBar) view.findViewById(
                R.id.little_video_record_progress);
        mProgress.setMax(MAX_TIME);
        initialProgress();
    }


    private void initialProgress() {
        mProgress.setProgress(0);
        mProgress.setSecondaryProgress(mProgress.getMax());
        mPro = mProgress.getProgress();
        mProSecond = mProgress.getMax();
    }


    private void clearTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }


    //设置Surface的宽度和高度
    private void measureSurfaceView() {
        RelativeLayout.LayoutParams lp
                = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenWidth;

        mSurfaceView.setLayoutParams(lp);

        measureBottomLayout(false);
    }


    private void measureBottomLayout(boolean show) {
        if (show) {
            ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin
                    = (mScreenWidth +
                    (int) getResources().getDimension(R.dimen.y3));
            if (mProgress != null) mProgress.setVisibility(View.VISIBLE);
        }
        else {
            ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin
                    = mScreenWidth;
            if (mProgress != null) mProgress.setVisibility(View.GONE);
        }
    }


    //长按开始录制
    private View.OnTouchListener mOnVideoControllerTouchListener
            = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mScaleAnimation == null) {
                        mScaleAnimation = AnimationUtils.loadAnimation(
                                mBaseActivity, R.anim.press_to_record);
                    }
                    mRecordController.startAnimation(mScaleAnimation);
                    mScaleAnimation.setFillAfter(true);
                    measureBottomLayout(true);
                    startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    stopRecord();
                    mRecordController.clearAnimation();
                    initialProgress();
                    measureBottomLayout(false);
                    break;
            }
            return true;
        }
    };

    //预览区域的 聚焦效果
    private View.OnTouchListener mOnSurfaceViewTouchListener
            = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            if (!mIsCreated || mMediaRecorder == null) {
                Log.e(tag, "initialize fail");
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (checkCameraFocus(event)) {
                        return true;
                    }
                    break;
            }
            return true;
        }
    };


    @Override public void onVideoError(int what, int extra) {

    }


    @Override public void onAudioError(int what, String message) {

    }


    //开始录制
    private void startRecord() {
        clearTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override public void run() {
                Message message = mHandler.obtainMessage();
                message.what = HANDLE_PROGRESS;
                mHandler.sendMessage(message);
            }
        };
        mTimer.schedule(mTimerTask, 100, 100);
    }


    //结束录制
    private void stopRecord() {
        clearTimer();
    }


    private boolean checkCameraFocus(MotionEvent event) {
        mFocusImage.setVisibility(View.GONE);
        //获取聚焦区域
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();
        Rect touchRect = new Rect((int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));

        if (touchRect.right > 1000) touchRect.right = 1000;
        if (touchRect.bottom > 1000) touchRect.bottom = 1000;
        if (touchRect.left < 0) touchRect.left = 0;
        if (touchRect.right < 0) touchRect.right = 0;

        if (touchRect.left >= touchRect.right ||
                touchRect.top >= touchRect.bottom) {
            return false;
        }

        //进行聚焦
        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(touchRect, 1000));
        if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {
            @Override public void onAutoFocus(boolean success, Camera camera) {
                mFocusImage.setVisibility(View.GONE);
            }
        }, focusAreas)) {
            mFocusImage.setVisibility(View.GONE);
        }

        //显示并设置聚焦图片
        RelativeLayout.LayoutParams lp
                = (RelativeLayout.LayoutParams) mFocusImage.getLayoutParams();
        int left = touchRect.left - mFocusWidth / 2;
        int top = touchRect.top - mFocusWidth / 2;
        if (left < 0) {
            left = 0;
        }
        else if (left + mFocusWidth > mScreenWidth) {
            left = mScreenWidth - mFocusWidth;
        }
        if (top + mFocusWidth > mScreenWidth) {
            top = mScreenWidth - mFocusWidth;
        }
        lp.leftMargin = left;
        lp.topMargin = top;
        mFocusImage.setLayoutParams(lp);
        mFocusImage.setVisibility(View.VISIBLE);

        //设置聚焦的动画
        if (mFocusAnimation == null) {
            mFocusAnimation = AnimationUtils.loadAnimation(mBaseActivity,
                    R.anim.record_focus);
        }
        mFocusImage.startAnimation(mFocusAnimation);
        mHandler.sendEmptyMessageAtTime(HANDLE_HIDE_RECORD_FOCUS, 3000);

        return true;
    }


    //类似于微信：背景黑色，圆环和字体绿色
    private TextDrawable getPressToRecordDrawable() {
        TextDrawable pressToRecord = TextDrawable.builder()
                                                 .beginConfig()
                                                 .fontSize(50)
                                                 .withBorder(10)
                                                 .width(500)
                                                 .height(500)
                                                 .textColor(Color.GREEN)
                                                 .borderColor(Color.GREEN)
                                                 .endConfig()
                                                 .buildRound(getString(
                                                         R.string.press_to_record),
                                                         Color.BLACK);

        return pressToRecord;
    }


    static class LittleVideoHandler extends Handler {
        private WeakReference<LittleVideoFragment> mWeakReference;


        public LittleVideoHandler(LittleVideoFragment fragment) {
            mWeakReference = new WeakReference<LittleVideoFragment>(fragment);
        }


        @Override public void handleMessage(Message msg) {
            LittleVideoFragment fragment = mWeakReference.get();
            if (fragment == null) return;

            switch (msg.what) {
                case HANDLE_HIDE_RECORD_FOCUS:
                    break;
                case HANDLE_PROGRESS:
                    fragment.mProgress.setProgress(fragment.mPro += 1);
                    fragment.mProgress.setSecondaryProgress(
                            fragment.mProSecond -= 1);

                    if (fragment.mPro == fragment.mProSecond) {
                        fragment.clearTimer();
                    }
                    break;
            }
        }
    }
}

