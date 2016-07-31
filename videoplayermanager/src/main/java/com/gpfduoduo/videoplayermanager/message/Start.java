package com.gpfduoduo.videoplayermanager.message;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/28.
 */

public class Start extends PlayerMessage {

    private PlayerMessageState mResultPlayerMessageState;


    public Start(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        super(currentPlayer, callback);
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        PlayerMessageState currentState = getCurrentState();

        switch (currentState) {
            case SETTING_NEW_PLAYER:
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case RELEASING:
            case RELEASED:
            case RESETTING:
            case RESET:
            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:
            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
            case PLAYBACK_COMPLETED:
            case END:
                throw new RuntimeException(
                        "unhandled current state " + currentState);

            case STARTING:
                Log.d(tag, "starting");
                currentPlayer.start();
                mResultPlayerMessageState = PlayerMessageState.STARTED;
                break;

            case ERROR:
                mResultPlayerMessageState = PlayerMessageState.ERROR;
                break;
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
                throw new RuntimeException(
                        "unhandled current state " + currentState);
        }
    }


    @Override protected PlayerMessageState stateBefore() {
        PlayerMessageState result = null;
        PlayerMessageState currentState = getCurrentState();
        switch (currentState) {
            case PREPARED:
                result = PlayerMessageState.STARTING;
                break;

            case SETTING_NEW_PLAYER:
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case RELEASING:
            case RELEASED:
            case RESETTING:
            case RESET:
            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:
            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
            case PLAYBACK_COMPLETED:
            case END:
            case STARTING:
                throw new RuntimeException(
                        "unhandled current state " + currentState);

            case ERROR:
                result = PlayerMessageState.ERROR;
                break;
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
                throw new RuntimeException(
                        "unhandled current state " + currentState);
        }
        return result;
    }


    @Override protected PlayerMessageState stateAfter() {
        return mResultPlayerMessageState;
    }
}
