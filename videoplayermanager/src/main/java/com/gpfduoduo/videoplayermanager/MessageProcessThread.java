package com.gpfduoduo.videoplayermanager;

import com.gpfduoduo.videoplayermanager.message.Message;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gpfduoduo on 2016/7/28.
 * see link{https://github.com/danylovolokh/VideoPlayerManager}
 */

public class MessageProcessThread {

    private static final String tag
            = MessageProcessThread.class.getSimpleName();
    private final Queue<Message> mPlayerMessageQueue
            = new ConcurrentLinkedQueue<>();
    private final PlayerQueueLock mQueueLock = new PlayerQueueLock();
    private final Executor mQueueProcessingThread
            = Executors.newSingleThreadExecutor();
    private AtomicBoolean mTerminated = new AtomicBoolean(false);
    private Message mLastMessage;


    public MessageProcessThread() {
        mQueueProcessingThread.execute(new Runnable() {
            @Override public void run() {
                do {
                    mQueueLock.lock(tag);
                    if (mPlayerMessageQueue.isEmpty()) {
                        try {
                            mQueueLock.wait(tag);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("InterruptedException");
                        }
                    }
                    mLastMessage = mPlayerMessageQueue.poll(); //返回并且移除队列的头部元素
                    mLastMessage.polledFromQueue();
                    mQueueLock.unLock(tag);

                    mLastMessage.runMessage();

                    mQueueLock.lock(tag);
                    mLastMessage.messageFinished();
                    mQueueLock.unLock(tag);
                } while (!mTerminated.get());
            }
        });
    }


    public void addMessage(Message message) {
        mQueueLock.lock(tag);
        mPlayerMessageQueue.add(message);
        mQueueLock.notify(tag);
        mQueueLock.unLock(tag);
    }


    public void addMessages(List<? extends Message> messages) {
        mQueueLock.lock(tag);
        mPlayerMessageQueue.addAll(messages);
        mQueueLock.notify(tag);
        mQueueLock.unLock(tag);
    }


    public void pauseQueueProcessing() {
        mQueueLock.lock(tag);
    }


    public void resumeQueueProcessing() {
        mQueueLock.unLock(tag);
    }


    public void clearAllPendingMessages() {
        if (mQueueLock.isLocked(tag)) {
            mPlayerMessageQueue.clear();
        }
        else {
            throw new RuntimeException("you are not holding a lock");
        }
    }


    public void terminate() {
        mTerminated.set(true);
    }
}
