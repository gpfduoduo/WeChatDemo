package com.gpfduoduo.videoplayermanager.manager;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.MessageProcessThread;
import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.message.ClearPlayerInstance;
import com.gpfduoduo.videoplayermanager.message.CreateNewPlayerInstance;
import com.gpfduoduo.videoplayermanager.message.Prepare;
import com.gpfduoduo.videoplayermanager.message.Release;
import com.gpfduoduo.videoplayermanager.message.Reset;
import com.gpfduoduo.videoplayermanager.message.SetNewViewForPlayback;
import com.gpfduoduo.videoplayermanager.message.Start;
import com.gpfduoduo.videoplayermanager.message.Stop;
import com.gpfduoduo.videoplayermanager.message.setLocalDataSource;
import com.gpfduoduo.videoplayermanager.view.MediaPlayerWrapper;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;
import java.util.Arrays;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class SingleVideoPlayerManager implements VideoPlayerManager,
        MediaPlayerWrapper.MainThreadMediaPlayerListener,
        VideoPlayerManagerCallback {

    private static final String tag
            = SingleVideoPlayerManager.class.getSimpleName();

    private final MessageProcessThread mPlayerHandler
            = new MessageProcessThread();
    private VideoPlayerView mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;
    private PlayerItemChangeListener mPlayerItemChangeListener;


    public SingleVideoPlayerManager() {

    }


    public SingleVideoPlayerManager(PlayerItemChangeListener listener) {
        mPlayerItemChangeListener = listener;
    }


    @Override
    public void playNewVideo(VideoPlayerView videoPlayerView, String videoUrl) {
        mPlayerHandler.pauseQueueProcessing();
        boolean currentPlayerIsActive = mCurrentPlayer == videoPlayerView;
        boolean isAlreadyPlayingFile = mCurrentPlayer != null &&
                videoUrl.equals(mCurrentPlayer.getVideoUrlDataSource());

        Log.d(tag, "play new video current play active is state = " +
                currentPlayerIsActive + "; isAlreadyPlayingFile = " +
                isAlreadyPlayingFile);

        if (currentPlayerIsActive) {
            if (isInPlaybackState() && isAlreadyPlayingFile) {
                Log.d(tag, videoUrl + " is in playing file, we not process");
            }
            else {
                startNewPlayback(videoPlayerView, videoUrl);
            }
        }
        else {
            startNewPlayback(videoPlayerView, videoUrl);
        }
        mPlayerHandler.resumeQueueProcessing();
    }


    @Override public void stopAnyPlayback() {
        mPlayerHandler.pauseQueueProcessing();

        mPlayerHandler.clearAllPendingMessages();
        stopResetReleaseClearCurrentPlayer();
        mPlayerHandler.resumeQueueProcessing();
    }


    @Override public void resetMediaPlayer() {
        mPlayerHandler.pauseQueueProcessing();
        mPlayerHandler.clearAllPendingMessages();
        resetReleaseClearCurrentPlayer();
        mPlayerHandler.resumeQueueProcessing();
    }


    @Override public void onVideoSizeChangedMainThread(int width, int height) {

    }


    @Override public void onVideoPreparedMainThread() {

    }


    @Override public void onVideoCompletionMainThread() {
        Log.d(tag, "video completed");
        mCurrentPlayerState = PlayerMessageState.PLAYBACK_COMPLETED;
    }


    @Override public void onErrorMainThread(int what, int extra) {
        Log.d(tag, "error");
        mCurrentPlayerState = PlayerMessageState.ERROR;
    }


    @Override public void onBufferingUpdateMainThread(int percent) {

    }


    @Override public void onVideoStoppedMainThread() {

    }


    @Override public void setCurrentItem(VideoPlayerView newPlayerView) {
        mCurrentPlayer = newPlayerView;
        if (mPlayerItemChangeListener != null) {
            mPlayerItemChangeListener.onPlayerItemChanged();
        }
    }


    @Override
    public void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState) {
        mCurrentPlayerState = playerMessageState;
    }


    @Override public PlayerMessageState getCurrentPlayerState() {
        return mCurrentPlayerState;
    }


    private void startNewPlayback(VideoPlayerView videoPlayerView, String videoUrl) {
        Log.d(tag, "start new playback");
        videoPlayerView.addMediaPlayerListener(this);
        mPlayerHandler.clearAllPendingMessages();
        stopResetReleaseClearCurrentPlayer();
        setNewViewForPlayback(videoPlayerView);
        startPlayback(videoPlayerView, videoUrl);
    }


    private void stopResetReleaseClearCurrentPlayer() {
        Log.d(tag, "stop reset release clear current player, current state = " +
                mCurrentPlayerState);

        switch (mCurrentPlayerState) {
            case SETTING_NEW_PLAYER:
            case IDLE:
            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:
            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                break;

            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
                mPlayerHandler.addMessage(new Stop(mCurrentPlayer, this));

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
            case STOPPING:
            case STOPPED:
            case ERROR:
            case PLAYBACK_COMPLETED:
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));

            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));

            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(
                        new ClearPlayerInstance(mCurrentPlayer, this));
                break;

            case END:
                Log.e(tag, "unhandled state = " + mCurrentPlayerState);
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }


    private void setNewViewForPlayback(VideoPlayerView videoPlayerView) {
        mPlayerHandler.addMessage(
                new SetNewViewForPlayback(videoPlayerView, this));
    }


    private void startPlayback(VideoPlayerView videoPlayerView, String videoUrl) {
        Log.d(tag, "start play back = " + videoUrl);
        mPlayerHandler.addMessages(Arrays.asList(
                new CreateNewPlayerInstance(videoPlayerView, this),
                new setLocalDataSource(videoPlayerView, this, videoUrl),
                new Prepare(videoPlayerView, this),
                new Start(videoPlayerView, this)));
    }


    private void resetReleaseClearCurrentPlayer() {
        Log.d(tag, "resetReleaseClearCurrentPlayer");
        switch (mCurrentPlayerState) {
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
            case ERROR:
            case PLAYBACK_COMPLETED:
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));
            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(
                        new ClearPlayerInstance(mCurrentPlayer, this));
                break;
            case END:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }


    private boolean isInPlaybackState() {
        boolean isPlaying = mCurrentPlayerState ==
                PlayerMessageState.PlayerMessageState.STARTED ||
                mCurrentPlayerState == PlayerMessageState.STARTING;
        return isPlaying;
    }
}
