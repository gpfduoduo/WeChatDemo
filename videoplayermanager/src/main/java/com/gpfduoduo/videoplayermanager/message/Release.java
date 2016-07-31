package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public class Release extends PlayerMessage {
    public Release(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.release();
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RELEASING;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RELEASED;
    }
}
