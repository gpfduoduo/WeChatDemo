package com.gpfduoduo.videoplayermanager.message;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public class Reset extends PlayerMessage {
    public Reset(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        Log.d(tag, "reset");
        currentPlayer.reset();
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RESETTING;
    }


    @Override protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RESET;
    }
}
