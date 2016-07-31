package com.gpfduoduo.videoplayermanager.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import com.gpfduoduo.videoplayermanager.utils.HandlerThreadExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gpfduoduo on 2016/7/27.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class VideoPlayerView extends ScaledTextureView
        implements TextureView.SurfaceTextureListener,
        MediaPlayerWrapper.MainThreadMediaPlayerListener,
        MediaPlayerWrapper.VideoStateListener {

    private static final String tag = VideoPlayerView.class.getSimpleName();

    private HandlerThreadExtension mViewHandlerBackgroundThread;
    private final ReadyForPlaybackIndicator mReadyForPlaybackIndicator
            = new ReadyForPlaybackIndicator();
    private BackgroundThreadMediaPlayerListener
            mMediaPlayerListenerBackgroundThread;
    private SurfaceTextureListener mLocalSurfaceTextureListener;
    private MediaPlayerWrapper mMediaPlayer;
    private final Set<MediaPlayerWrapper.MainThreadMediaPlayerListener>
            mMediaPlayerMainThreadListeners = new HashSet<>();

    private String mPath;

    public interface BackgroundThreadMediaPlayerListener {
        void onVideoSizeChangedBackgroundThread(int width, int height);

        void onVideoPreparedBackgroundThread();

        void onVideoCompletionBackgroundThread();

        void onErrorBackgroundThread(int what, int extra);
    }


    public VideoPlayerView(Context context) {
        super(context);
        init();
    }


    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            setScaleType(ScaleType.CENTER_CROP);
            super.setSurfaceTextureListener(this);
        }
    }


    public void createNewPlayerInstance() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            Log.d(tag, "createNewPlayerInstance");
            mMediaPlayer = new MediaPlayerWrapper(new MediaPlayer());
            mReadyForPlaybackIndicator.setVideoSize(null, null);
            mReadyForPlaybackIndicator.setFailedToPrepareUiForPlayback(false);
            if (mReadyForPlaybackIndicator.isSurfaceTextureAvailable()) {
                SurfaceTexture texture = getSurfaceTexture();
                mMediaPlayer.setSurfaceTexture(texture);
            }
            else {
                Log.d(tag, "texture is not available");
            }
            mMediaPlayer.setMainThreadMediaPlayerListener(this);
            mMediaPlayer.setVideoStateListener(this);
        }
    }


    public void clearPlayerInstance() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mReadyForPlaybackIndicator.setVideoSize(null, null);
            mMediaPlayer.clearAll();
            mMediaPlayer = null;
        }
    }


    public String getVideoUrlDataSource() {
        return mPath;
    }


    public void setDataSource(String path) {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            try {
                mMediaPlayer.setDataSource(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mPath = path;
        }
    }


    public void prepare() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.prepare();
        }
    }


    public void stop() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.stop();
        }
    }


    public void reset() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            Log.d(tag, "reset");
            mMediaPlayer.reset();
        }
    }


    public void release() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            Log.d(tag, "release");
            mMediaPlayer.release();
        }
    }


    public void start() {
        Log.d(tag, "start");
        synchronized (mReadyForPlaybackIndicator) {
            if (mReadyForPlaybackIndicator.isReadyForPlayback()) {
                Log.d(tag, "start to play");
                mMediaPlayer.start();
            }
            else {
                if (!mReadyForPlaybackIndicator.isFailedToPrepareUiForPlayback()) {
                    try {
                        Log.d(tag, "wait");
                        mReadyForPlaybackIndicator.wait();
                    } catch (InterruptedException e) {
                        Log.e(tag, "wait error = " + e.getMessage().toString());
                        throw new RuntimeException(e);
                    }

                    if (mReadyForPlaybackIndicator.isReadyForPlayback()) {
                        Log.d(tag, "isReadyForPlayback to start playing");
                        mMediaPlayer.start();
                    }
                    else {
                        Log.e(tag, "is not ReadyForPlayback");
                    }
                }
                else {
                    Log.e(tag, "isFailedToPrepareUiForPlayback");
                }
            }
        }
    }


    public MediaPlayerWrapper.State getCurrentState() {
        synchronized (mReadyForPlaybackIndicator) {
            return mMediaPlayer.getCurrentState();
        }
    }


    @Override public void onVideoSizeChangedMainThread(int width, int height) {
        Log.d(tag, "onVideoSizeChanged width = " + width + "; height = " +
                height);

        if (width != 0 && height != 0) {
            setContentWidth(width);
            setContentHeight(height);
            onVideoSizeAvailable();
        }
        else {
            synchronized (mReadyForPlaybackIndicator) {
                mReadyForPlaybackIndicator.setFailedToPrepareUiForPlayback(
                        true); //tru才对
                mReadyForPlaybackIndicator.notifyAll();
            }
        }
        notifyOnVideoSizeChangedMainThread(width, height);
    }


    @Override public void onVideoPreparedMainThread() {
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners) {
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoPreparedMainThread();
        }
    }


    @Override public void onVideoCompletionMainThread() {
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners) {
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoCompletionMainThread();
        }
    }


    @Override public void onErrorMainThread(int what, int extra) {
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners) {
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onErrorMainThread(what, extra);
        }
    }


    @Override public void onBufferingUpdateMainThread(int percent) {

    }


    @Override public void onVideoStoppedMainThread() {
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners) {
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoStoppedMainThread();
        }
    }


    @Override public void onVideoPlayTimeChanged(int positionInMilliseconds) {

    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mLocalSurfaceTextureListener != null) {
            mLocalSurfaceTextureListener.onSurfaceTextureAvailable(surface,
                    width, height);
        }
        notifyTextureAvailable();
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mLocalSurfaceTextureListener != null) {
            mLocalSurfaceTextureListener.onSurfaceTextureSizeChanged(surface,
                    width, height);
        }
    }


    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mLocalSurfaceTextureListener != null) {
            mLocalSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
        }

        if (isAttachedToWindow()) {
            mViewHandlerBackgroundThread.post(new Runnable() {
                @Override public void run() {
                    synchronized (mReadyForPlaybackIndicator) {
                        mReadyForPlaybackIndicator.setSurfaceTextureAvailable(
                                false);
                        mReadyForPlaybackIndicator.notifyAll();
                    }
                }
            });
        }
        surface.release();
        return false;
    }


    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (mLocalSurfaceTextureListener != null) {
            mLocalSurfaceTextureListener.onSurfaceTextureUpdated(surface);
        }
    }


    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean isInEditMode = isInEditMode();
        if (!isInEditMode) {
            mViewHandlerBackgroundThread = new HandlerThreadExtension(tag,
                    false);
            mViewHandlerBackgroundThread.startThread();
        }
    }


    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!isInEditMode()) {
            mViewHandlerBackgroundThread.postQuit();
            mViewHandlerBackgroundThread = null;
        }
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (!isInEditMode()) {
            switch (visibility) {
                case VISIBLE:
                    break;
                case INVISIBLE:
                case GONE:
                    synchronized (mReadyForPlaybackIndicator) {
                        mReadyForPlaybackIndicator.notifyAll();
                    }
            }
        }
    }


    private void onVideoSizeAvailable() {
        updateTextureViewSize();
        if (isAttachedToWindow()) {
            mViewHandlerBackgroundThread.post(new Runnable() {
                @Override public void run() {
                    synchronized (mReadyForPlaybackIndicator) {
                        mReadyForPlaybackIndicator.setVideoSize(
                                getContentHeight(), getContentWidth());
                        if (mReadyForPlaybackIndicator.isReadyForPlayback()) {
                            mReadyForPlaybackIndicator.notifyAll();
                        }
                    }
                    if (mMediaPlayerListenerBackgroundThread != null) {
                        mMediaPlayerListenerBackgroundThread.onVideoSizeChangedBackgroundThread(
                                getContentWidth(), getContentHeight());
                    }
                }
            });
        }
    }


    @Override public boolean isAttachedToWindow() {
        return mViewHandlerBackgroundThread != null;
    }


    private void notifyOnVideoSizeChangedMainThread(int width, int height) {
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners) {
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoSizeChangedMainThread(width, height);
        }
    }


    public final void setSurfaceTextureListener(SurfaceTextureListener listener) {
        mLocalSurfaceTextureListener = listener;
    }


    public void addMediaPlayerListener(MediaPlayerWrapper.MainThreadMediaPlayerListener listener) {
        synchronized (mMediaPlayerMainThreadListeners) {
            mMediaPlayerMainThreadListeners.add(listener);
        }
    }


    private void notifyTextureAvailable() {
        mViewHandlerBackgroundThread.post(new Runnable() {
            @Override public void run() {
                synchronized (mReadyForPlaybackIndicator) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.setSurfaceTexture(getSurfaceTexture());
                    }
                    else {
                        mReadyForPlaybackIndicator.setVideoSize(null, null);
                    }
                    mReadyForPlaybackIndicator.setSurfaceTextureAvailable(true);
                    if (mReadyForPlaybackIndicator.isReadyForPlayback()) {
                        mReadyForPlaybackIndicator.notifyAll();
                    }
                }
            }
        });
    }


    private void checkThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not be main thread");
        }
    }
}
