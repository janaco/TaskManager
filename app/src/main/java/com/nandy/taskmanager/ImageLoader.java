package com.nandy.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by razomer on 19.01.18.
 */

public class ImageLoader {

    public static RequestBuilder<Drawable> load(Context context, String pathToImage, @IdRes int overlayResId) {

        Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), overlayResId);

        return Glide.with(context)
                .load(pathToImage)
                .apply(new RequestOptions()
                        .centerCrop()
                        .transform(new OverlayTransformation(overlay, context.getResources().getDisplayMetrics()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true));
    }

    public static RequestBuilder<Drawable> load(Context context, String pathToImage) {

        return Glide.with(context)
                .load(pathToImage)
                .apply(new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true));
    }
}
