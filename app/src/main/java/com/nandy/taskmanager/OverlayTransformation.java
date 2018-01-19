package com.nandy.taskmanager;

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
import android.util.TypedValue;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by razomer on 19.01.18.
 */

public class OverlayTransformation extends BitmapTransformation {

    private static final float RADIUS_CORNER_PX = 10;

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

        int padding = mBitmapOverlay.getHeight()/5;
        int width = resultBitmap.getWidth() - padding;
        int height = resultBitmap.getHeight() - padding;
        roundCorners(canvas, mDisplayMetrics, source, width, height, RADIUS_CORNER_PX );

        int overlaySize = mBitmapOverlay.getHeight() - padding;
        Bitmap overlay = resize(mBitmapOverlay, overlaySize, overlaySize);
        overlay = drawOverlay(overlay, mBitmapOverlay.getHeight(), mBitmapOverlay.getHeight());

        int left = canvas.getWidth() - overlay.getWidth();
        int top = canvas.getHeight() - overlay.getHeight();

        canvas.drawBitmap(overlay, left, top, new Paint());

        return resultBitmap;
    }

    private void roundCorners(Canvas canvas, DisplayMetrics displayMetrics, Bitmap source, int width, int height, float cornersRadius){

        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornersRadius,
               displayMetrics);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
    }

    private Bitmap resize(Bitmap source, int width, int height) {
        int originWidth = source.getWidth();
        int originHeight = source.getHeight();
        float scaleWidth = ((float) width) / originWidth;
        float scaleHeight = ((float) height) / originHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                source, 0, 0, originWidth, originHeight, matrix, false);
        source.recycle();
        return resizedBitmap;
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


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        try {
            messageDigest.update("Overlay Transformation".getBytes(STRING_CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}