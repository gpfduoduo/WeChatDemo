package com.gpfduoduo.videoplayermanager.manager;

import com.gpfduoduo.videoplayermanager.view.VideoPlayerView;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public interface VideoPlayerManager {
    void playNewVideo(VideoPlayerView videoPlayerView, String videoUrl);

    void stopAnyPlayback();

    void resetMediaPlayer();
}
