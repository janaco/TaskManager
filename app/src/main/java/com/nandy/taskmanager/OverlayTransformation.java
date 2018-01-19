package com.nandy.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by razomer on 19.01.18.
 */

public class OverlayTransformation extends BitmapTransformation {

    private Bitmap mBitmapOverlay;
    private DisplayMetrics mDisplayMetrics;

    public OverlayTransformation(Bitmap bitmapOverlay, DisplayMetrics displayMetrics) {
        mBitmapOverlay = bitmapOverlay;
        mDisplayMetrics = displayMetrics;
    }


    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {

        Bitmap resultBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);

        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 9, mDisplayMetrics);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, resultBitmap.getWidth() - mBitmapOverlay.getHeight()/2, resultBitmap.getHeight() - mBitmapOverlay.getHeight()/2);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);


        Log.d("IMAGE_TRANSFORMATION_", "source: " + source + ", overlay: " + mBitmapOverlay);

        int overlaySize = mBitmapOverlay.getHeight() + mBitmapOverlay.getHeight()/5;
        Bitmap overlay = drawOverlay(mBitmapOverlay, overlaySize, overlaySize);

        int left = canvas.getWidth() - overlay.getWidth();
        int top = canvas.getHeight() - overlay.getHeight();

        canvas.drawBitmap(overlay, left, top, new Paint());

        return resultBitmap;
    }

    private Bitmap drawOverlay(Bitmap overlay, int resWidth, int resHeight){

        Bitmap result = Bitmap.createBitmap(resWidth, resHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);

        int centerX = canvas.getWidth()/2;
        int centerY = canvas.getHeight()/2;
        canvas.drawCircle(centerX, centerX, radius, paint);

        int left = centerX - overlay.getWidth()/2;
        int top = centerY - overlay.getHeight()/2;
        canvas.drawBitmap(overlay, left, top, new Paint());

        return result;
    }

//    private  Bitmap transform( Bitmap source) {
//        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(output);
//
//        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 9, mDisplayMetrics);
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
//        final RectF rectF = new RectF(rect);
//
//        paint.setAntiAlias(true);
//        paint.setColor(0xFFFFFFFF);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(source, rect, rect, paint);
//
//        return output;
//    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        try {
            messageDigest.update("Overlay Transformation".getBytes(STRING_CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

//        extends BitmapTransformation {
//    private static final String ID = "com.nandy.taskmanager.OverlayTransformation";
//
//
//    @Override
//    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
//
//        Bitmap resultBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
//        Canvas canvas = new Canvas(resultBitmap);
//        canvas.drawBitmap(source, new Matrix(), null);
//        canvas.drawBitmap(mBitmapOverlay, (source.getWidth() - mBitmapOverlay.getWidth()) / 2,
//                (source.getHeight() - mBitmapOverlay.getHeight()) / 2, new Paint());
//
//        return resultBitmap;
//    }
//
//    @Override
//    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
//        try {
//            messageDigest.update(ID.getBytes(STRING_CHARSET_NAME));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//}
