package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.gpfduoduo.wechat.ui.fragment.weichat.CameraFragment;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {

    WeakReference<CameraFragment> activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;


    DecodeThread(CameraFragment activity) {
        this.activity = new WeakReference<CameraFragment>(activity);
        handlerInitLatch = new CountDownLatch(1);
    }


    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }


    @Override public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }


    public void cancel() {
        Message message = handler.obtainMessage();
        message.what = DecodeHandler.QUIT;
        handler.sendMessage(message);
    }
}
