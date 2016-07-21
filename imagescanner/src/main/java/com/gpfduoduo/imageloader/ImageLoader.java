package com.gpfduoduo.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地图片加载类
 */
public class ImageLoader {
    private static final String tag = ImageLoader.class.getSimpleName();

    private static int threadCount =
            Runtime.getRuntime().availableProcessors() + 1;

    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;
    private Looper mLooper;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    private static ImageLoader mInstance;

    /**
     * 队列的调度方式
     */
    public enum Type {
        FIFO, LIFO
    }

    private static final int GET_IMAGE = 0x110;
    private static final int EXIT = 0x111;


    public static ImageLoader getInstance() {
        return getInstance(threadCount, Type.FIFO);
    }


    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }


    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }


    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread("JT_ImageLoader") {
            @Override public void run() {
                Looper.prepare();
                mLooper = Looper.myLooper();
                //XLog.d(tag, "loop thread run");
                mPoolThreadHander = new MyPoolThreadHandler(ImageLoader.this);
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取应用程序最大可用内存
        if (mLruCache != null) {
            mLruCache.evictAll();
            mLruCache = null;
        }

        mLruCache = new LruCache<String, Bitmap>(
                (int) Runtime.getRuntime().maxMemory() / 4) {
            @Override protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);


            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "JT_ImageLoader_ThreadPool #" +
                        mCount.getAndIncrement());
                return thread;
            }
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount, threadFactory);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;
    }


    /**
     * 重新设置缓存的大小
     *
     * @param size 单位字节
     */
    public void resetCacheSize(int size) {
        mLruCache.trimToSize(size);
    }


    public void clearCache() {
        if (mLruCache != null) mLruCache.evictAll();
    }


    /**
     * 加载图片
     */
    public void loadImage(final String path, final ImageView imageView) {
        if (TextUtils.isEmpty(path)) return;
        // set tag
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) mHandler = new MyHandler();

        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            imageView.setImageBitmap(bm);
        }
        else {
            addTask(new Runnable() {
                @Override public void run() {
                    Process.setThreadPriority(
                            Process.THREAD_PRIORITY_BACKGROUND);
                    try {
                        ImageUtils.ImageSize imageSize
                                = ImageUtils.getImageViewWidth(imageView);

                        int reqWidth = imageSize.width;
                        int reqHeight = imageSize.height;

                        Bitmap bm = null;
                        if (path.endsWith(".mp4")) {
                            bm = ImageUtils.getVideoThumbNail(path);
                        }
                        else {
                            bm = ImageUtils.decodeSampledBitmapFromResource(
                                    path, reqWidth, reqHeight);
                        }
                        addBitmapToLruCache(path, bm);
                        ImgBeanHolder holder = new ImgBeanHolder();
                        holder.bitmap = bm; //getBitmapFromLruCache(path);
                        holder.imageView = new WeakReference<ImageView>(
                                imageView);
                        holder.path = path;
                        holder.isBigImg = false;
                        Message message = Message.obtain();
                        message.obj = holder;
                        mHandler.sendMessage(message);
                        mPoolSemaphore.release();
                    } catch (OutOfMemoryError e) {
                        mPoolSemaphore.release();
                    } catch (IOException e) {
                        mPoolSemaphore.release();
                    }
                }
            });
        }
    }


    /**
     * 添加一个任务
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null) mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(GET_IMAGE);
    }


    /**
     * 退出
     */
    public void quit() {
        if (mPoolThreadHander != null) mPoolThreadHander.sendEmptyMessage(EXIT);
    }


    /**
     * 取出一个任务
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        }
        else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }


    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }


    /**
     * 往LruCache中添加一张图片
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null) mLruCache.put(key, bitmap);
        }
    }


    private class ImgBeanHolder {
        Bitmap bitmap;
        WeakReference<ProgressBar> progressBar;
        WeakReference<ImageView> imageView;
        String path;
        boolean isBigImg = false;
    }

    /**
     * 处理得到的图像
     */
    private static class MyHandler extends Handler {
        @Override public void handleMessage(Message msg) {
            ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
            if (holder.imageView == null) {
                return;
            }

            ImageView imageView = holder.imageView.get();
            if (imageView == null) {
                return;
            }

            Bitmap bm = holder.bitmap;
            String path = holder.path;
            if (imageView.getTag().toString().equals(path)) {
                if (bm == null) {
                }
                else {
                    imageView.setImageBitmap(bm);
                    imageView.setBackgroundResource(
                            android.R.color.transparent);
                }
            }
            else {
            }

            if (holder.isBigImg) {
                if (holder.progressBar == null) return;
                ProgressBar progressBar = holder.progressBar.get();
                if (progressBar == null) return;

                if (progressBar.getTag().toString().equals(path)) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    private static class MyPoolThreadHandler extends Handler {

        WeakReference<ImageLoader> weakReference;


        public MyPoolThreadHandler(ImageLoader imageLoader) {
            weakReference = new WeakReference<ImageLoader>(imageLoader);
        }


        @Override public void handleMessage(Message msg) {
            if (msg == null) return;
            if (weakReference.get() == null) return;

            switch (msg.what) {
                case GET_IMAGE:
                    weakReference.get().mThreadPool.execute(
                            weakReference.get().getTask());
                    try {
                        weakReference.get().mPoolSemaphore.acquire();
                    } catch (InterruptedException e) {
                    }
                    break;
                case EXIT:
                    weakReference.get().mLooper.quit();
                    weakReference.get().clearCache();
                    if (weakReference.get() != null) {
                        weakReference.get().mPoolThread.interrupt();
                        weakReference.get().mPoolThread = null;
                        if (weakReference.get().mPoolThreadHander != null) {
                            weakReference.get().mPoolThreadHander.removeCallbacksAndMessages(
                                    null);
                        }
                        if (weakReference.get().mThreadPool != null) {
                            weakReference.get().mThreadPool.shutdown();
                        }
                        if (weakReference.get().mHandler != null) {
                            weakReference.get().mHandler.removeCallbacksAndMessages(
                                    null);
                        }
                        if (weakReference.get().mTasks != null) {
                            weakReference.get().mTasks.clear();
                        }
                    }
                    mInstance = null;
                    break;
            }
        }


        ;
    }
}
