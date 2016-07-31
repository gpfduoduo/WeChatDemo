package com.gpfduoduo.videoplayermanager.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class HandlerThreadExtension extends HandlerThread {

    private static final String TAG
            = HandlerThreadExtension.class.getSimpleName();

    private Handler mHandler;
    private final Object mStart = new Object();


    /**
     * @param name
     * @param setupExceptionHandler
     */
    public HandlerThreadExtension(String name, boolean setupExceptionHandler) {
        super(name);
        if (setupExceptionHandler) {
            setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            });
        }
    }


    @Override protected void onLooperPrepared() {
        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override public void run() {
                synchronized (mStart) {
                    mStart.notifyAll();
                }
            }
        });
    }


    public void post(Runnable r) {
        boolean successfullyAddedToQueue = mHandler.post(r);
    }


    public void postAtFrontOfQueue(Runnable r) {
        mHandler.postAtFrontOfQueue(r);
    }


    public void startThread() {
        synchronized (mStart) {
            start();
            try {
                mStart.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void postQuit() {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Looper.myLooper().quit();
            }
        });
    }


    public void remove(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }
}

