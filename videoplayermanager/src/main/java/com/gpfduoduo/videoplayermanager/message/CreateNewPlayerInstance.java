package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class CreateNewPlayerInstance extends PlayerMessage {

    public CreateNewPlayerInstance(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.createNewPlayerInstance();
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CREATING_PLAYER_INSTANCE;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CREATED;
    }
}
