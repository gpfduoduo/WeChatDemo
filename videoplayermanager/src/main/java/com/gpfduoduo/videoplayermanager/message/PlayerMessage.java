package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

public abstract class PlayerMessage implements Message {

    protected static final String tag = Message.class.getSimpleName();

    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;


    public PlayerMessage(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = currentPlayer;
        mCallback = callback;
    }


    protected final PlayerMessageState getCurrentState() {
        return mCallback.getCurrentPlayerState();
    }


    @Override public final void polledFromQueue() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateBefore());
    }


    @Override public final void messageFinished() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateAfter());
    }


    public final void runMessage() {
        performAction(mCurrentPlayer);
    }


    protected abstract void performAction(VideoPlayerView currentPlayer);

    protected abstract PlayerMessageState stateBefore();

    protected abstract PlayerMessageState stateAfter();
}
