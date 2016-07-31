package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

public class SetNewViewForPlayback extends PlayerMessage {

    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;


    public SetNewViewForPlayback(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mCurrentPlayer = videoPlayerView;
        mCallback = callback;
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        mCallback.setCurrentItem(mCurrentPlayer);
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_NEW_PLAYER;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.IDLE;
    }
}
