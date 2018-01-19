package com.nandy.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by razomer on 19.01.18.
 */

public class ImageLoader {

    public static RequestBuilder<Drawable> load(Context context, String pathToImage, int overlayResId) {

        Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), overlayResId);

        return Glide.with(context)
                .load(pathToImage)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_task)
                        .error(R.mipmap.ic_task)
                        .centerCrop()
                        .transform(new OverlayTransformation(overlay, context.getResources().getDisplayMetrics()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true));
    }

    public static RequestBuilder<Drawable> load(Context context, String pathToImage) {

        return Glide.with(context)
                .load(pathToImage)
                .apply(getDefaultRequestOptions()
                        .transform(new RoundedCornersTransformation(context.getResources().getDisplayMetrics())));
    }

    public static RequestBuilder<Drawable> load(Context context, int imageResId, int overlayResId) {

        Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), overlayResId);

        return Glide.with(context)
                .load(imageResId)
                .apply(getDefaultRequestOptions()
                        .transform(new OverlayTransformation(overlay, context.getResources().getDisplayMetrics()))
                );
    }

    public static RequestBuilder<Drawable> load(Context context, int imageResId) {
        return Glide.with(context)
                .load(imageResId)
                .apply(getDefaultRequestOptions()
                        .transform(new RoundedCornersTransformation(context.getResources().getDisplayMetrics())));
    }

    private static RequestOptions getDefaultRequestOptions() {
        return new RequestOptions()
                .placeholder(R.mipmap.ic_task)
                .error(R.mipmap.ic_task)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
    }
}
