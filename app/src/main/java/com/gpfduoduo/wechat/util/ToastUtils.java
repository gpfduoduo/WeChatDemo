package com.gpfduoduo.wechat.util;

import android.widget.Toast;
import com.gpfduoduo.wechat.MyApplication;

public class ToastUtils {
    private static Toast toast;


    public static void showToast(int resId) {
        showToast(MyApplication.getContext().getString(resId));
    }


    public static void showToast(String msg) {
        cancelToast();
        toast = Toast.makeText(MyApplication.getContext(), msg,
                Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
