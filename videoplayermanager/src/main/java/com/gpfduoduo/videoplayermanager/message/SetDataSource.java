package com.gpfduoduo.videoplayermanager.message;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public abstract class SetDataSource extends PlayerMessage {
    public SetDataSource(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_DATA_SOURCE;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.DATA_SOURCE_SET;
    }
}
