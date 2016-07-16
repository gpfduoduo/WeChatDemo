package com.gpfduoduo.wechat.ui.fragment.weichat;

import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.gpfduoduo.wechat.MyApplication;
import com.gpfduoduo.wechat.R;
import com.gpfduoduo.wechat.ui.fragment.BaseBackFragment;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gpfduoduo on 2016/6/28.
 */
public class CameraFragment extends BaseBackFragment
        implements SurfaceHolder.Callback {

    private static final String LEFT_TITLE = "left_title";
    private static final long VIBRATE_DURATION = 200L;
    private static final int QR_SCAN_TIMEOUT = 3 * 1000;
    private static final float BEEP_VOLUME = 0.50f;

    private CameraHandler mCameraHandler;
    private CaptureActivityHandler mHandler;
    private Timer mScanTimer = null;
    private TimerTask mScanTimerTask = null;
    private MediaPlayer mediaPlayer;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private ViewStub mStub;
    private SurfaceHolder mSurfaceHolder;
    private View mView;
    private String mLeftTitle;

    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;

    private boolean isNeedCapture = false;
    private boolean hasSurface;
    private boolean playBeep;
    private boolean vibrate;


    public boolean isNeedCapture() {
        return isNeedCapture;
    }


    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }


    public int getX() {
        return x;
    }


    public void setX(int x) {
        this.x = x;
    }


    public int getY() {
        return y;
    }


    public void setY(int y) {
        this.y = y;
    }


    public int getCropWidth() {
        return cropWidth;
    }


    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }


    public int getCropHeight() {
        return cropHeight;
    }


    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }


    public Handler getHandler() {
        return mHandler;
    }


    public static CameraFragment newInstance(String leftTitle) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(LEFT_TITLE, leftTitle);
        fragment.setArguments(args);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLeftTitle = bundle.getString(LEFT_TITLE);
        }

        CameraManager.init(MyApplication.getContext());
        mCameraHandler = new CameraHandler(this);
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_camera, container, false);
        initToolbar(mView);
        return mView;
    }


    private void initToolbar(View view) {
        Toolbar toolbar = initToolBar(view, R.id.camera_title_viewstub, -1,
                getString(R.string.action_qrcode), mLeftTitle);
        initToolbarNav(toolbar);
    }


    private void startScanAnimation(View view) {
        ImageView mQrLineView = (ImageView) view.findViewById(
                R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
                0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
    }


    @Override public void onResume() {
        super.onResume();
        mCameraHandler.postDelayed(new Runnable() {
            @Override public void run() {
                if (mBaseActivity.isFinishing()) {
                    return;
                }
                initSurfaceView(mView);
            }
        }, 500);
    }


    private void initSurfaceView(View view) {
        mStub = (ViewStub) view.findViewById(R.id.camera_viewstub);
        if (null != mStub) {
            RelativeLayout layout = (RelativeLayout) mStub.inflate();

            mContainer = (RelativeLayout) layout.findViewById(
                    R.id.capture_containter);
            mCropLayout = (RelativeLayout) layout.findViewById(
                    R.id.capture_crop_layout);

            SurfaceView surfaceView = (SurfaceView) layout.findViewById(
                    R.id.capture_preview);
            mSurfaceHolder = surfaceView.getHolder();
            startScanAnimation(view);
        }

        if (hasSurface) {
            initCamera(mSurfaceHolder);
        }
        else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) MyApplication.getContext()
                                                                .getSystemService(
                                                                        MyApplication
                                                                                .getContext().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }


    /**
     * QR scan result
     */
    public void handleDecode(String result) {
        playBeepSoundAndVibrate();
        Toast.makeText(MyApplication.getContext(), result, Toast.LENGTH_SHORT)
             .show();
        restartQr();
    }


    private void restartQr() {
        clearTimer();
        mScanTimer = new Timer();
        mScanTimerTask = new TimerTask() {
            @Override public void run() {
                clearTimer();
                if (mHandler == null) return;

                mBaseActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(
                                    R.id.restart_preview);// 连续扫描
                        }
                    }
                });
            }
        };

        mScanTimer.schedule(mScanTimerTask, QR_SCAN_TIMEOUT);
    }


    private void clearTimer() {
        if (mScanTimer != null) mScanTimer.cancel();
        if (mScanTimerTask != null) mScanTimerTask.cancel();
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width /
                    mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height /
                    mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(false);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (mHandler == null) {
            mHandler = new CaptureActivityHandler(this);
        }
    }


    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            mBaseActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }


    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) MyApplication.getContext()
                                                        .getSystemService(
                                                                MyApplication.getContext().VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    private final MediaPlayer.OnCompletionListener beepListener
            = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }


    @Override public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        CameraManager.get().closeDriver();
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        clearTimer();
    }


    class CameraHandler extends Handler {
        private WeakReference<CameraFragment> mWeakReference;


        public CameraHandler(CameraFragment fragment) {
            mWeakReference = new WeakReference<CameraFragment>(fragment);
        }


        public void handleMessage(Message msg) {
        }
    }
}
