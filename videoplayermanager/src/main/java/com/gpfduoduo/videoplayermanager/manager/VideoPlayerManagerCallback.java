package com.gpfduoduo.videoplayermanager.manager;

import com.gpfduoduo.videoplayermanager.PlayerMessageState;
import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
