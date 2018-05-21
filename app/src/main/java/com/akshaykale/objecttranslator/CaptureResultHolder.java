package com.akshaykale.objecttranslator;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.wonderkiln.camerakit.Size;

import java.lang.ref.WeakReference;

class CaptureResultHolder {

    private static WeakReference<Bitmap> image;
    private static Size nativeCaptureSize;
    private static long timeToCallback;


    public static void setImage(@Nullable Bitmap image) {
        CaptureResultHolder.image = image != null ? new WeakReference<>(image) : null;
    }

    @Nullable
    public static Bitmap getImage() {
        return image != null ? image.get() : null;
    }

    public static void setNativeCaptureSize(@Nullable Size nativeCaptureSize) {
        CaptureResultHolder.nativeCaptureSize = nativeCaptureSize;
    }

    @Nullable
    public static Size getNativeCaptureSize() {
        return nativeCaptureSize;
    }

    public static void setTimeToCallback(long timeToCallback) {
        CaptureResultHolder.timeToCallback = timeToCallback;
    }

    public static long getTimeToCallback() {
        return timeToCallback;
    }

    public static void dispose() {
        setImage(null);
        setNativeCaptureSize(null);
        setTimeToCallback(0);
    }

}
