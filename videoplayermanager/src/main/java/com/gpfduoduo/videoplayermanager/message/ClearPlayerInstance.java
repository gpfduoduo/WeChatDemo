package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public class ClearPlayerInstance extends PlayerMessage {
    public ClearPlayerInstance(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.clearPlayerInstance();
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CLEARING_PLAYER_INSTANCE;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CLEARED;
    }
}
