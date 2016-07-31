package com.gpfduoduo.videoplayermanager.message;

import android.util.Log;
import com.gpfduoduo.videoplayermanager.manager.VideoPlayerManagerCallback;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by Administrator on 2016/7/29.
 */

public class setLocalDataSource extends SetDataSource {

    private String mVideoPath;


    public setLocalDataSource(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback, String videoPath) {
        super(currentPlayer, callback);
        this.mVideoPath = videoPath;
    }


    @Override protected void performAction(VideoPlayerView currentPlayer) {
        Log.d(tag, "setLocalDataSource = " + mVideoPath);
        currentPlayer.setDataSource(mVideoPath);
    }
}
