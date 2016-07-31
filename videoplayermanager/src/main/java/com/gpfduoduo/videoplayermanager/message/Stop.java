package com.gpfduoduo.videoplayermanager.message;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public class Stop extends PlayerMessage {

    public Stop(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        Log.d(tag, "stop");
        currentPlayer.stop();
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.STOPPING;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.STOPPED;
    }
}
