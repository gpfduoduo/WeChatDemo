package com.gpfduoduo.videoplayermanager.view;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.gpfduoduo.videoplayermanager.view.MediaPlayerWrapper.Error.ERROR_PREPARE;

/**
 * Created by gpfduoduo on 2016/7/27.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class MediaPlayerWrapper
        implements MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener {

    private static final String tag = MediaPlayerWrapper.class.getSimpleName();

    private static final int POSITION_UPDATE_NOTIFYING_PERIOD = 1000;
    private static final int ERROR_WRAPPER = 0;

    public interface Error {
        public static final int ERROR_PREPARE = -1;
    }

    private MainThreadMediaPlayerListener mMainThreadMediaPlayerListener;
    private VideoStateListener mVideoStateListener;

    private final MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private final AtomicReference<State> mState = new AtomicReference<>();
    private final Handler mMainThreadHandler = new Handler(
            Looper.getMainLooper());
    private ScheduledFuture<?> mFuture; //定时周期性的任务
    private ScheduledExecutorService mPositionUpdateNotifier
            = Executors.newScheduledThreadPool(1);

    public interface MainThreadMediaPlayerListener {
        void onVideoSizeChangedMainThread(int width, int height);

        void onVideoPreparedMainThread();

        void onVideoCompletionMainThread();

        void onErrorMainThread(int what, int extra);

        void onBufferingUpdateMainThread(int percent);

        void onVideoStoppedMainThread();
    }

    public interface VideoStateListener {
        void onVideoPlayTimeChanged(int positionInMilliseconds);
    }

    public enum State {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED,
        PLAYBACK_COMPLETED, END, ERROR
    }


    public MediaPlayerWrapper(MediaPlayer mediaPlayer) {
        if (Looper.myLooper() != null) {
            throw new RuntimeException("my looper not null");
        }
        mMediaPlayer = mediaPlayer;
        mState.set(State.IDLE);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
    }


    @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mMainThreadMediaPlayerListener != null) {
            mMainThreadMediaPlayerListener.onBufferingUpdateMainThread(percent);
        }
    }


    @Override public void onCompletion(MediaPlayer mp) {
        synchronized (mState) {
            mState.set(State.PLAYBACK_COMPLETED);
        }
        if (mMainThreadMediaPlayerListener != null) {
            mMainThreadMediaPlayerListener.onVideoCompletionMainThread();
        }
    }


    @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(tag, "onError");
        synchronized (mState) {
            mState.set(State.ERROR);
        }
        if (positionUpdaterIsWorking()) {
            stopPositionUpdateNotifier();
        }
        if (mMainThreadMediaPlayerListener != null) {
            mMainThreadMediaPlayerListener.onErrorMainThread(what, extra);
        }
        return true;
    }


    @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (!inUiThread()) {
            throw new RuntimeException("this should be called in Main Thread");
        }

        if (mMainThreadMediaPlayerListener != null) {
            mMainThreadMediaPlayerListener.onVideoSizeChangedMainThread(width,
                    height);
        }
    }


    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (surfaceTexture != null) {
            mSurface = new Surface(surfaceTexture);
            mMediaPlayer.setSurface(mSurface);
        }
        else {
            mMediaPlayer.setSurface(null);
        }
    }


    public void setMainThreadMediaPlayerListener(MainThreadMediaPlayerListener listener) {
        mMainThreadMediaPlayerListener = listener;
    }


    public void setVideoStateListener(VideoStateListener listener) {
        mVideoStateListener = listener;
    }


    public void setDataSource(String filePath) throws IOException {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                    mMediaPlayer.setDataSource(filePath);
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    throw new IllegalStateException(
                            "setDataSource called in state " + mState);
            }
        }
    }


    public void prepare() {
        synchronized (mState) {
            switch (mState.get()) {
                case STOPPED:
                case INITIALIZED:
                    try {
                        mMediaPlayer.prepare();
                        mState.set(State.PREPARED);
                        if (mMainThreadMediaPlayerListener != null) {
                            mMainThreadHandler.post(new Runnable() {
                                @Override public void run() {
                                    mMainThreadMediaPlayerListener.onVideoPreparedMainThread();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e(tag, e.getMessage().toString());
                        mState.set(State.ERROR);
                        if (mMainThreadMediaPlayerListener != null) {
                            mMainThreadHandler.post(new Runnable() {
                                @Override public void run() {
                                    mMainThreadMediaPlayerListener.onErrorMainThread(
                                            ERROR_WRAPPER, ERROR_PREPARE);
                                }
                            });
                        }
                    }
                    break;
                case IDLE:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                    throw new IllegalStateException(
                            "prepare, called from illegal state " + mState);
            }
        }
    }


    public void start() {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case STARTED:
                    Log.e(tag, "start, called from illegal state = " + mState);
                    throw new IllegalStateException(
                            "start, called from illegal state " + mState);
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PAUSED:
                    mMediaPlayer.start();
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);
                    break;
                case ERROR:
                case END:
                    Log.e(tag, "start, called from illegal state = " + mState);
                    throw new IllegalStateException(
                            "start, called from illegal state " + mState);
            }
        }
    }


    public void stop() {
        synchronized (mState) {
            switch (mState.get()) {
                case STARTED:
                case PAUSED:
                    stopPositionUpdateNotifier();
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING:
                    Log.d(tag, "stop");
                    mMediaPlayer.stop();
                    mState.set(State.STOPPED);
                    if (mVideoStateListener != null) {
                        mMainThreadHandler.post(new Runnable() {
                            @Override public void run() {
                                mMainThreadMediaPlayerListener.onVideoStoppedMainThread();
                            }
                        });
                    }
                    break;
                case STOPPED:
                    Log.d(tag, "stop, already stopped");
                    throw new IllegalStateException("stop, already stopped");
                case IDLE:
                case INITIALIZED:
                case END:
                case ERROR:
                    Log.e(tag, "can not stop, player in state =  " + mState);
                    throw new IllegalStateException(
                            "cannot stop. Player in mState " + mState);
            }
        }
    }


    public void pause() {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case PREPARED:
                case END:
                    Log.e(tag, "pause, called from illegal state = " + mState);
                    throw new IllegalStateException(
                            "pause, called from illegal state " + mState);

                case STARTED:
                    mMediaPlayer.pause();
                    mState.set(State.PAUSED);
                    break;
            }
        }
    }


    public void reset() {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                    mMediaPlayer.reset();
                    mState.set(State.IDLE);
                    break;
                case PREPARING:
                case END:
                    Log.e(tag,
                            " reset , called from illegal state = " + mState);
                    throw new IllegalStateException(
                            "cannot call reset from state " + mState.get());
            }
        }
    }


    public void release() {
        synchronized (mState) {
            mMediaPlayer.release();
            mState.set(State.END);
        }
    }


    public void clearAll() {
        synchronized (mState) {
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnInfoListener(null);
        }
    }


    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }


    public State getCurrentState() {
        synchronized (mState) {
            return mState.get();
        }
    }


    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }


    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }


    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }


    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    public boolean isReadyForPlayback() {
        boolean isReadyForPlayback = false;
        synchronized (mState) {
            State state = mState.get();
            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case END:
                    isReadyForPlayback = false;
                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    isReadyForPlayback = true;
                    break;
            }
        }
        return isReadyForPlayback;
    }


    public int getDuration() {
        int duration = 0;
        synchronized (mState) {
            switch (mState.get()) {
                case END:
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case ERROR:
                    duration = 0;
                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                    duration = mMediaPlayer.getDuration();
            }
        }
        return duration;
    }


    private void startPositionUpdateNotifier() {
        mFuture = mPositionUpdateNotifier.scheduleAtFixedRate(
                mNotifyPositionUpdateRunnable, 0,
                POSITION_UPDATE_NOTIFYING_PERIOD, TimeUnit.MILLISECONDS);
    }


    private void stopPositionUpdateNotifier() {
        mFuture.cancel(true);
        mFuture = null;
    }


    private boolean positionUpdaterIsWorking() {
        return mFuture != null;
    }


    private final Runnable mNotifyPositionUpdateRunnable = new Runnable() {
        @Override public void run() {
            notifyPositionUpdated();
        }
    };


    private void notifyPositionUpdated() {
        if (mVideoStateListener != null && mState.get() == State.STARTED) {
            mVideoStateListener.onVideoPlayTimeChanged(
                    mMediaPlayer.getCurrentPosition());
        }
    }


    private boolean inUiThread() {
        return Thread.currentThread().getId() == 1;
    }
}
