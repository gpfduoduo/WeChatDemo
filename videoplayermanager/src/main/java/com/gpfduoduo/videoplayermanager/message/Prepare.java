package com.gpfduoduo.videoplayermanager.message;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.MediaPlayerWrapper;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/28.
 */

public class Prepare extends PlayerMessage {

    private PlayerMessageState mResultPlayerMessageState;


    public Prepare(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        Log.d(tag, "prepare");
        currentPlayer.prepare();

        MediaPlayerWrapper.State resultOfPrepare
                = currentPlayer.getCurrentState();

        switch (resultOfPrepare) {
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case STARTED:
            case PAUSED:
            case STOPPED:
            case PLAYBACK_COMPLETED:
            case END:
                throw new RuntimeException(
                        "unhandled state " + resultOfPrepare);
            case PREPARED:
                mResultPlayerMessageState = PlayerMessageState.PREPARED;
                break;
            case ERROR:
                mResultPlayerMessageState = PlayerMessageState.ERROR;
                break;
        }
    }


    @Override protected PlayerMessageState stateBefore() {
        return PlayerMessageState.PREPARING;
    }


    @Override protected PlayerMessageState stateAfter() {
        return mResultPlayerMessageState;
    }
}
