package com.gpfduoduo.wechat.util.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by gpfduoduo on 2016/7/15.
 *
 * see link{https://muzhi1991.gitbooks.io/android-glide-wiki/content/index.html}
 */
public class SimpleGlideModule implements GlideModule {
    @Override public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
    }


    @Override public void registerComponents(Context context, Glide glide) {

    }
}
