package com.gpfduoduo.videoplayermanager;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class PlayerQueueLock {

    private static final String tag = PlayerQueueLock.class.getSimpleName();

    private final ReentrantLock mQueueLock = new ReentrantLock();
    private final Condition mProcessQueueConditions = mQueueLock.newCondition();


    public void lock(String owner) {
        mQueueLock.lock();
    }


    public void unLock(String owner) {
        mQueueLock.unlock();
    }


    public boolean isLocked(String owner) {
        boolean isLocked = mQueueLock.isLocked();
        return isLocked;
    }


    public void wait(String owner) throws InterruptedException {
        mProcessQueueConditions.await();
    }


    public void notify(String owner) {
        mProcessQueueConditions.signal();
    }
}
