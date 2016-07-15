package com.gpfduoduo.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/4.
 */
public class ImageScanner {

    private Context mContext;

    private HashSet<String> mDirPaths = new HashSet<String>();
    private List<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
    private int mPicsSize;
    private File mImgDir;
    private int totalCount = 0;

    public static interface ScanCompleteCallBack {
        public void scanComplete(File imgDir, List<ImageFolder> imageFolders);
    }


    public ImageScanner(Context context) {
        mContext = context;
    }


    public void scanImages(final ScanCompleteCallBack callback) {
        final Handler mHandler = new Handler() {

            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback != null) {
                    callback.scanComplete(mImgDir, mImageFolders);
                }
            }
        };

        new Thread(new Runnable() {
            @Override public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver
                        = mContext.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(
                            MediaStore.Images.Media.DATA));

                    // 拿到第一张图片的路径
                    if (firstImage == null) firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFolder imageFolder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    }
                    else {
                        mDirPaths.add(dirPath);
                        imageFolder = new ImageFolder();
                        imageFolder.setDir(dirPath);
                        imageFolder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") ||
                                    filename.endsWith(".png") ||
                                    filename.endsWith(".jpeg")) {
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    totalCount += picSize;

                    imageFolder.setCount(picSize);
                    mImageFolders.add(imageFolder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
                mDirPaths = null;

                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
