package com.gpfduoduo.wechat.util.camera;

import android.app.Activity;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gpfduoduo on 2016/7/21.
 */
public class MediaRecorderSystem extends MediaRecorderBase
        implements MediaRecorder.OnErrorListener {

    private final static String TIMESTAMP_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    private static final String VIDEO_FORMAT = ".mp4";
    private static final int AUDIO_ENCODING_BIT_RATE = 44100;

    private File mVideoFolder;
    private MediaRecorder mMediaRecorder;
    private File mVideoFile;


    public MediaRecorderSystem(Activity activity, File videoFolder, int width) {
        super(activity, width);
        mVideoFolder = videoFolder;
    }


    @Override protected void onStartPreviewSuccess() {
        super.onStartPreviewSuccess();
    }


    public void startRecord() {
        try {
            createVideoFile();

            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setOnErrorListener(this);
            }
            else {
                mMediaRecorder.reset();
            }
            //设置相机
            camera.unlock();//录制前需要解锁
            mMediaRecorder.setCamera(camera);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            //设置录制视频和音频
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOrientationHint(CameraUtil.getInstance()
                                                        .getCameraDisplayOrientation(
                                                                mContext,
                                                                mCameraId,
                                                                camera));
            //让手机竖屏播放
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置录制格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            CamcorderProfile profile = CamcorderProfile.get(
                    CamcorderProfile.QUALITY_480P);
            mMediaRecorder.setVideoSize(mPreviewWidth, mPreviewHeight);
            mMediaRecorder.setAudioEncodingBitRate(AUDIO_ENCODING_BIT_RATE);

            if (profile.videoBitRate > 2 * 1024 * 1024) {
                mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
            }
            else {
                mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
            }

            mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);

            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            mMediaRecorder.setOutputFile(mVideoFile.getAbsolutePath());

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mRecording = true;
        } catch (IOException e) {
            File errorFile = getVideoFile();
            if (errorFile == null) {
                return;
            }
            if (errorFile.exists()) {
                errorFile.delete();
            }
        }
    }


    public void stopRecord() {
        if (mMediaRecorder != null) {
            //设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
            } catch (RuntimeException e) {
            } catch (Exception e) {
            }
        }

        if (camera != null) {
            try {
                camera.lock();
            } catch (RuntimeException e) {
            }
        }

        mRecording = false;
    }


    public void release() {
        super.release();
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
            } catch (Exception e) {
            }
        }
        mMediaRecorder = null;
    }


    @Override public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) mr.reset();
        } catch (IllegalStateException e) {
        } catch (Exception e) {
        }
        if (mOnErrorListener != null) {
            mOnErrorListener.onVideoError(what, extra);
        }
    }


    public int getPreviewWidth() {
        return mPreviewWidth;
    }


    public int getPreviewHeight() {
        return mPreviewHeight;
    }


    public File getVideoFile() {
        return mVideoFile;
    }


    private void createVideoFile() {
        if (mVideoFolder != null) {
            if (!mVideoFolder.exists()) {//检查保存图片的目录存不存在
                mVideoFolder.mkdirs();
            }

            String fileName = new SimpleDateFormat(TIMESTAMP_FORMAT).format(
                    new Date());
            mVideoFile = new File(mVideoFolder, fileName + VIDEO_FORMAT);
            if (mVideoFile.exists()) {
                mVideoFile.delete();
            }
            try {
                mVideoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                mVideoFile = null;
            }
        }
        else {
            mVideoFile = null;
        }
    }
}

