package com.gpfduoduo.videoplayermanager.message;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public interface Message {

    void runMessage();

    void polledFromQueue();

    void messageFinished();
}
